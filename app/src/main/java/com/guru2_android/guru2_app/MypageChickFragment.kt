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

        // ???????????? ????????? ??????
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.value.toString()

                text.text = "${nickname} ??? ????????? ?????????!"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        // ???????????? ?????? ??????
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            Toast.makeText(getActivity(), "???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // ?????? ???????????? ????????? egg ??????
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

        // ?????? ?????? ????????? activity??? ??????
        last_message.setOnClickListener {
            val intent = Intent(getActivity(), LastMessageActivity::class.java)
            startActivity(intent)
        }

        // ????????? egg??? ???????????? egg ????????? ???????????? activity??? ??????
        eggLayout.setOnClickListener {
            val intent = Intent(getActivity(), ChickEggActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        // ?????? ????????? ????????? ????????? ?????? ??? ?????? ?????? ????????? ????????? recentMessage ????????? ??????
        init {
            FirebaseDatabase.getInstance().reference.child(uid).child("message")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val item = data.getValue<messageModel>()
                            message.add(item!!)
                        }
                        message.reverse()   // ?????? ??????
                        if (message.size != 0) {    // ????????? ????????? ?????? ?????? ?????? ????????? ????????? ??????
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
            holder.textDate.text = recentMessage.date   // ????????? ?????? ??????
            holder.textChat.text = recentMessage.mess   // ????????? ??????
        }

        override fun getItemCount(): Int {
            if (message.size != 0) {    // ????????? ????????? ????????? ?????? ?????? ????????? ?????? ??????
                return 1
            }
            return message.size
        }

    }

}