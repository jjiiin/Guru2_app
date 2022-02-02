package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.auth.LoginActivity
import com.guru2_android.guru2_app.dataModel.messageModel

class MypageChickFragment : Fragment() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<messageModel> = arrayListOf()
    private var recentMessage = messageModel()

    lateinit var text: TextView
    lateinit var nickname: String
    lateinit var last_message: LinearLayout
    lateinit var eggLayout: LinearLayout
    lateinit var egg: TextView
    lateinit var logout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val reference: DatabaseReference =
            database.getReference("users").child(uid).child("nickname")
        val view = inflater.inflate(R.layout.fragment_mypage_chick, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.mypage_chat_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        text = view.findViewById(R.id.mypage_chick_text)
        last_message = view.findViewById(R.id.chick_last_message_layout)
        eggLayout = view.findViewById(R.id.chick_egg_layout)
        egg = view.findViewById(R.id.chick_egg)
        logout = view.findViewById(R.id.mypage_chick_logout)

        // 병아리의 닉네임 출력
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.value.toString()

                text.text = "${nickname} 님 오늘도 화이팅!"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        // 로그아웃 버튼 클릭
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 현재 병아리가 소유한 egg 출력
        database.reference.child(uid).child("egg").child("totalEgg")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        var item = data.value
                        egg.text = item.toString() + "egg"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        // 지난 칭찬 메세지 activity로 이동
        last_message.setOnClickListener {
            val intent = Intent(getActivity(), LastMessageActivity::class.java)
            startActivity(intent)
        }

        // 병아리 egg를 클릭하면 egg 내역을 확인하는 activity로 이동
        eggLayout.setOnClickListener {
            val intent = Intent(getActivity(), ChickEggActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        // 받은 메세지 목록을 배열에 담은 뒤 가장 최근 메세지 하나만 recentMessage 변수에 담음
        init {
            FirebaseDatabase.getInstance().reference.child(uid).child("message")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val item = data.getValue<messageModel>()
                            message.add(item!!)
                        }
                        message.reverse()   // 순서 반전
                        if (message.size != 0) {    // 메세지 목록이 있을 경우 최근 메세지 변수에 담음
                            recentMessage = message[0]
                        }
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapter.CustomViewHolder {
            return CustomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textDate: TextView = itemView.findViewById(R.id.message_item_date)
            val textChat: TextView = itemView.findViewById(R.id.message_item_text)
        }

        override fun onBindViewHolder(holder: RecyclerViewAdapter.CustomViewHolder, position: Int) {
            holder.textDate.text = recentMessage.date   // 메세지 받은 날짜
            holder.textChat.text = recentMessage.mess   // 메세지 내용
        }

        override fun getItemCount(): Int {
            if (message.size != 0) {    // 메세지 목록이 존재할 때만 최근 메세지 하나 출력
                return 1
            }
            return message.size
        }

    }

}