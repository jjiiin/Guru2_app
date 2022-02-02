package com.guru2_android.guru2_app

import android.os.Build
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
import com.guru2_android.guru2_app.dataModel.eggModel
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
    lateinit var egg: String    // 현재 보유한 egg
    lateinit var chickenEggBack: ImageView

    private var eggArray: ArrayList<eggModel> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chicken_egg)

        chickName = findViewById(R.id.chicken_egg_chick_name)
        currentEgg = findViewById(R.id.current_egg) // 현재 보유한 egg
        confirmBtn = findViewById(R.id.btn_confirm)
        editEgg = findViewById(R.id.edit_egg)
        contents = findViewById(R.id.contents)
        chickenEggBack = findViewById(R.id.chicken_egg_back)
        chickUid = intent.getStringExtra("chickUid").toString() // 인텐트로 병아리 uid 받아 옴

        val recyclerView = findViewById<RecyclerView>(R.id.chicken_egg_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        // 뒤로 가기 버튼 클릭시 finish
        chickenEggBack.setOnClickListener {
            finish()
        }

        // 병아리 이름 가져오기
        FirebaseDatabase.getInstance().reference.child("users").child(chickUid)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nickname = snapshot.child("nickname").value
                    chickName.text = nickname.toString()

                    // 병아리가 현재 보유한 에그 출력
                    FirebaseDatabase.getInstance().reference.child(chickUid).child("egg")
                        .child("totalEgg")
                        .addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                egg = snapshot.child("egg").value.toString()
                                if (egg == "null") {    //egg가 없을 경우 0으로 지정
                                    egg = "0"
                                }
                                currentEgg.text = "현재 ${nickname} 님이 보유한 에그 : ${egg} egg"
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        // 확인 버튼을 누르면 egg 내역 추가, total egg 차감
        confirmBtn.setOnClickListener {
            // 날짜 형식 지정
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            time = current.format(formatter)
            val formatter2 = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val date = current.format(formatter2)

            // egg 차감 내역 firebase에 추가
            val eggRef = database.reference.child(chickUid).child("egg")
            val eggModel = eggModel(date, "-" + editEgg.text.toString(), contents.text.toString())
            eggRef.push().setValue(eggModel)

            // total egg 수정
            val changeEgg = egg.toInt() - Integer.parseInt(editEgg.text.toString())
            database.reference.child(chickUid).child("egg")
                .child("totalEgg").child("egg").setValue(changeEgg.toString())

            // editText 초기화
            editEgg.setText("")
            contents.setText("")
        }

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            // egg 내역을 배열에 담음
            FirebaseDatabase.getInstance().reference.child(chickUid).child("egg")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        eggArray.clear()
                        for (data in snapshot.children) {
                            if (data.key != "totalEgg") {   // totalEgg를 제외 한 egg 내역을 eggArray에 담음
                                val item = data.getValue<eggModel>()
                                eggArray.add(item!!)
                            }
                        }
                        eggArray.reverse()
                        notifyDataSetChanged()  // 값이 변경되었음을 알림
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
            holder.textDate.text = eggArray[position].date  // egg가 수정된 날짜
            holder.textContents.text = eggArray[position].title // egg 차감, 증가 사유
            holder.textEgg.text = eggArray[position].egg    // 차감, 증가한 egg
        }

        override fun getItemCount(): Int {
            return eggArray.size
        }

    }

}