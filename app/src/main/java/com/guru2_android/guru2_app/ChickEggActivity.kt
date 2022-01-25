package com.guru2_android.guru2_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class ChickEggActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()

    private var history: ArrayList<History> = arrayListOf()
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

        chickEggBack.setOnClickListener {
            finish()
        }

        FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val data2 = data.child("child")
                    if (data2.value != null) {
                        for (data3 in data2.children) {
                            if (data3.key == uid) {
                                currentEggText.text = "현재 보유한 에그 : "+data3.child("info/point").getValue().toString() + "egg"

                                val data4 = data3.child("history")
                                for (data5 in data4.children) {
                                    val item = data5.getValue<History>()
                                    history.add(item!!)
                                }
                            }
                        }
                    }
                }
                history.reverse()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

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
            holder.textDate.text = history[position].date
            holder.textContents.text = history[position].contents
            holder.textEgg.text = history[position].calc
        }

        override fun getItemCount(): Int {
            return history.size
        }

    }
}