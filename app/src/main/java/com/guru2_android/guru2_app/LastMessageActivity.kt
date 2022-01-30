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
import com.guru2_android.guru2_app.dataModel.messageModel

class LastMessageActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private var message: ArrayList<messageModel> = arrayListOf()
    lateinit var lastMessageBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_message)

        // 뒤로 가기 버튼
        lastMessageBack = findViewById(R.id.last_message_back)
        lastMessageBack.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.chick_last_message_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()


    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<CustomViewHolder>() {

        // 받은 칭찬 메세지 목록을 message 배열에 담음
        init {
            FirebaseDatabase.getInstance().reference.child(uid).child("message")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val item = data.getValue<messageModel>()
                            message.add(item!!)
                        }
                        message.reverse()   // 최근 순으로 재정렬
                        notifyDataSetChanged()
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
            val textDate: TextView = itemView.findViewById(R.id.message_item_date)
            val textChat: TextView = itemView.findViewById(R.id.message_item_text)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textDate.text = message[position].date
            holder.textChat.text = message[position].mess
        }

        override fun getItemCount(): Int {
            return message.size
        }

    }
}