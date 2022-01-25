package com.guru2_android.guru2_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LastMessageChickenActivity : AppCompatActivity() {

    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<MessageModel> = arrayListOf()
    lateinit var chicken_nickname: String
    lateinit var lastMessageChickenBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_message_chicken)

        lastMessageChickenBack = findViewById(R.id.last_message_chicken_back)

        val recyclerView = findViewById<RecyclerView>(R.id.chicken_last_message_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        lastMessageChickenBack.setOnClickListener {
            finish()
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("quest").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        for (data2 in data.children) {
                            if (data2.key.toString() == uid) {
                                for (data3 in data2.children) {
                                    var data4 = data3.child("firm")
                                    val item = data4.getValue<MessageModel>()
                                    message.add(item!!)
                                }
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
            val inflatedView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message_chick, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textName: TextView = itemView.findViewById(R.id.message_item_name)
            val textDate: TextView = itemView.findViewById(R.id.message_item_date)
            val textChat: TextView = itemView.findViewById(R.id.message_item_text)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textName.text = message[position].chick_nickname
            holder.textDate.text = message[position].time
            holder.textChat.text = message[position].message
        }

        override fun getItemCount(): Int {
            return message.size
        }

    }
}