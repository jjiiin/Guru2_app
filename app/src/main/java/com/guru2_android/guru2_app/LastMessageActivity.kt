package com.guru2_android.guru2_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.LastMessageActivity.RecyclerViewAdapter.CustomViewHolder

class LastMessageActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<MessageModel> = arrayListOf()
    lateinit var lastMessageBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_message)

        lastMessageBack = findViewById(R.id.last_message_back)

        val recyclerView = findViewById<RecyclerView>(R.id.chick_last_message_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        lastMessageBack.setOnClickListener {
            finish()
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("quest").child(uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(data in snapshot.children) {
                        for (data2 in data.children) {
                            var data3 = data2.child("firm")
                            if (data3.getValue() != null) {
                                val item = data3.getValue<MessageModel>()
                                message.add(item!!)
                            }
                        }
                    }
                    message.reverse()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CustomViewHolder {
            val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textName: TextView = itemView.findViewById(R.id.message_item_name)
            val textDate: TextView = itemView.findViewById(R.id.message_item_date)
            val textChat: TextView = itemView.findViewById(R.id.message_item_text)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textName.text = message[position].chicken_nickname
            holder.textDate.text = message[position].time
            holder.textChat.text = message[position].message
        }

        override fun getItemCount(): Int {
            return message.size
        }

    }
}