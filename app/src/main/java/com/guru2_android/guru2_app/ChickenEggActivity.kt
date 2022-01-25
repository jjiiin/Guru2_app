package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChickenEggActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()

    lateinit var chickUid: String
    lateinit var chickName: TextView
    lateinit var currentEgg: TextView
    lateinit var confirmBtn: Button
    lateinit var editEgg: EditText
    lateinit var contents: EditText
    lateinit var time: String
    var history = History()
    private var historyArray: ArrayList<History> = arrayListOf()
    lateinit var egg: String
    lateinit var chickenEggBack: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chicken_egg)

        chickName = findViewById(R.id.chicken_egg_chick_name)
        currentEgg = findViewById(R.id.current_egg)
        confirmBtn = findViewById(R.id.btn_confirm)
        editEgg = findViewById(R.id.edit_egg)
        contents = findViewById(R.id.contents)
        chickenEggBack = findViewById(R.id.chicken_egg_back)
        chickUid = intent.getStringExtra("chickUid").toString()

        val recyclerView = findViewById<RecyclerView>(R.id.chicken_egg_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        chickenEggBack.setOnClickListener {
            finish()
        }

        FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        if (data.key == chickUid) {
                            val data2 = data.child("info")
                            val item = data2.getValue<ChickInfo>()
                            egg = item?.point.toString()

                            chickName.text = item?.nickname
                            currentEgg.text = "현재 ${item?.nickname} 님이 보유한 에그 : ${item?.point}egg"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        confirmBtn.setOnClickListener {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            time = current.format(formatter)
            val formatter2 = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val date = current.format(formatter2)

            val database =
                FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
                    .child(chickUid).child("history").child(time)

            database.child("calc").setValue("-" + editEgg.text.toString())
            database.child("contents").setValue(contents.text.toString())
            database.child("date").setValue(date.toString())

            val changeEgg = egg.toInt() - Integer.parseInt(editEgg.text.toString())

            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
                .child(chickUid).child("info").child("point").setValue(changeEgg.toString())

            editEgg.setText("")
            contents.setText("")
        }

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
                .child(chickUid).child("history")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val item = data.getValue<History>()
                            historyArray.add(item!!)
                        }
                        historyArray.reverse()
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
                LayoutInflater.from(parent.context).inflate(R.layout.item_egg, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textDate: TextView = itemView.findViewById(R.id.egg_date)
            val textContents: TextView = itemView.findViewById(R.id.egg_contents)
            val textEgg: TextView = itemView.findViewById(R.id.egg_op)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textDate.text = historyArray[position].date
            holder.textContents.text = historyArray[position].contents
            holder.textEgg.text = historyArray[position].calc
        }

        override fun getItemCount(): Int {
            return historyArray.size
        }

    }
}