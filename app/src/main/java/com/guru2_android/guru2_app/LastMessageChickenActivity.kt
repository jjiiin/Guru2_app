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
import com.guru2_android.guru2_app.dataModel.messChicken

class LastMessageChickenActivity : AppCompatActivity() {

    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<messChicken> = arrayListOf()
    lateinit var lastMessageChickenBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_message_chicken)

        // 뒤로 가기 버튼
        lastMessageChickenBack = findViewById(R.id.last_message_chicken_back)

        // 리사이클러뷰
        val recyclerView = findViewById<RecyclerView>(R.id.chicken_last_message_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        lastMessageChickenBack.setOnClickListener {
            finish()
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            // 병아리에게 보낸 메세지를 모델로 가져옴
            FirebaseDatabase.getInstance().reference.child(uid).child("message").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val item = data.getValue<messChicken>()
                        message.add(item!!)
                    }
                    message.reverse()   // 최신순으로 재정렬
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

            holder.textName.text = message[position].chickname  // 병아리 이름
            holder.textDate.text = message[position].date   // 메세지 보낸 날짜
            holder.textChat.text = message[position].mess   // 메세지 내용
        }

        override fun getItemCount(): Int {
            return message.size
        }

    }
}