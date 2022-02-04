package com.guru2_android.guru2_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.eggModel
import com.guru2_android.guru2_app.dataModel.messageModel

class ChickEggActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()

    private var history: ArrayList<eggModel> = arrayListOf()
    lateinit var currentEggText: TextView
    lateinit var chickEggBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chick_egg)

        currentEggText = findViewById(R.id.chick_current_egg)
        chickEggBack = findViewById(R.id.chick_egg_back)

        val recyclerView = findViewById<RecyclerView>(R.id.chick_egg_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        // 뒤로 가기 버튼 클릭
        chickEggBack.setOnClickListener {
            finish()
        }

        FirebaseDatabase.getInstance().reference.child(uid).child("egg").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    if (data.key == "totalEgg") {   // 현재 소유한 egg 출력
                        currentEggText.text = "현재 보유한 에그 : " + data.child("egg").value.toString() + "egg"
                    }
                    if (data.key != "totalEgg") {   // egg 내역을 배열에 담음
                        val item = data.getValue<eggModel>()
                        history.add(item!!)
                    }
                }
                history.reverse()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    // egg 내역을 RecyclerView로 출력
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CustomViewHolder {
            val inflatedView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_egg, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textDate: TextView = itemView.findViewById(R.id.egg_date)
            val textContents: TextView = itemView.findViewById(R.id.egg_contents)
            val textEgg: TextView = itemView.findViewById(R.id.egg_op)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textDate.text = history[position].date   // egg가 수정된 날짜
            holder.textContents.text = history[position].title  // egg 차감, 증가 사유
            holder.textEgg.text = history[position].egg // 차감, 증가된 egg
        }

        override fun getItemCount(): Int {
            return history.size
        }

    }
}