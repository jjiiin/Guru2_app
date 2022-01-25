package com.guru2_android.guru2_app

import android.content.Intent
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

class ChickListActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private var info: ArrayList<ChickInfo> = arrayListOf()
    lateinit var chickUid: String
    lateinit var chick_list_back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chick_list)

        chick_list_back = findViewById(R.id.chick_list_back)

        val recyclerView = findViewById<RecyclerView>(R.id.chick_list_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        chick_list_back.setOnClickListener {
            finish()
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val data2 = data.child("info")
                            val item = data2.getValue<ChickInfo>()
                            info.add(item!!)
                            chickUid = data.key.toString()
                        }
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
                LayoutInflater.from(parent.context).inflate(R.layout.item_chick_list, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textName: TextView = itemView.findViewById(R.id.chick_list_name)
            val textPoint: TextView = itemView.findViewById(R.id.chick_list_point)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textName.text = info[position].nickname
            holder.textPoint.text = info[position].point + "p"

            holder.itemView.setOnClickListener {
                val intent = Intent(this@ChickListActivity, ChickenEggActivity::class.java)
                intent.putExtra("chickUid", chickUid)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return info.size
        }

    }
}