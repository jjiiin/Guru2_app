package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MypageChickFragment : Fragment() {

    companion object {
        fun newInstance(): MypageChickFragment {
            return MypageChickFragment()
        }
    }

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<MessageModel> = arrayListOf()
    private var dateArray: ArrayList<String> = arrayListOf()

    lateinit var text: TextView
    lateinit var settings: ImageView
    lateinit var nickname: String
    lateinit var last_message: LinearLayout
    lateinit var eggLayout: LinearLayout
    lateinit var egg: TextView

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
        settings = view.findViewById(R.id.mypage_chick_settings)
        last_message = view.findViewById(R.id.chick_last_message_layout)
        eggLayout = view.findViewById(R.id.chick_egg_layout)
        egg = view.findViewById(R.id.chick_egg)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.value.toString()

                text.text = "${nickname} 님 오늘도 화이팅!"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val data2 = data.child("child")
                    if (data2.value != null) {
                        for (data3 in data2.children) {
                            if (data3.key == uid) {
                                val info = data3.child("info")
                                egg.text = info.child("point").value.toString() + "egg"
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        FirebaseDatabase.getInstance().reference.child("quest").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (data in snapshot.children) {
                        for (data2 in data.children) {
                            dateArray.add(data2.key.toString())
                        }
                    }
                    Log.d("tag", "${dateArray.last()}")
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        last_message.setOnClickListener {
            val intent = Intent(getActivity(), LastMessageActivity::class.java)
            startActivity(intent)
        }

        eggLayout.setOnClickListener {
            val intent = Intent(getActivity(), ChickEggActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("quest").child(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (data in snapshot.children) {
                            for (data2 in data.children) {
                                dateArray.add(data2.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

            FirebaseDatabase.getInstance().reference.child("quest").child(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (data in snapshot.children) {
                            for (data2 in data.children) {
                                if (data2.key.toString() == dateArray.last().toString()) {
                                    var data3 = data2.child("firm")
                                    if (data3.getValue() != null) {
                                        val item = data3.getValue<MessageModel>()
                                        message.add(item!!)
                                    }
                                }
                            }
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
            val textName: TextView = itemView.findViewById(R.id.message_item_name)
            val textDate: TextView = itemView.findViewById(R.id.message_item_date)
            val textChat: TextView = itemView.findViewById(R.id.message_item_text)
        }

        override fun onBindViewHolder(holder: RecyclerViewAdapter.CustomViewHolder, position: Int) {
            holder.textName.text = message[position].chicken_nickname
            holder.textDate.text = message[position].time
            holder.textChat.text = message[position].message
        }

        override fun getItemCount(): Int {
            return message.size
        }

    }

}