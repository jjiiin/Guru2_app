package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.chickModel

class MypageActivity : AppCompatActivity() {

    lateinit var group: String
    lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val reference: DatabaseReference =
            database.getReference("users").child(uid.toString()).child("group")

        // 뒤로 가기 버튼
        back = findViewById(R.id.mypage_back)

        // 뒤로 가기 버튼을 클릭했을 때
        back.setOnClickListener {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    group = snapshot.value.toString()

                    // 현재 사용자가 닭일 때 MainActivity2로 이동
                    if (group == "2") {
                        val intent = Intent(this@MypageActivity, MainActivity2::class.java)
                        startActivity(intent)
                        //현재 사용자가 병아리일 때 MainActivity로 이동
                    } else {
                        val intent = Intent(this@MypageActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                group = snapshot.value.toString()

                // 현재 사용자가 닭일 때 해당 Chicken fragment로 이동
                // 현재 사용자가 닭일 때 bundle로 병아리 리스트도 함께 보냄
                if (group == "2") {
                    val chickList = intent.getSerializableExtra("list") as ArrayList<chickModel>    //닭의 병아리 리스트
                    var chickenFragment = MypageChickenFragment()
                    var bundle1 = Bundle()
                    bundle1.putParcelableArrayList("list", chickList)
                    chickenFragment.arguments = bundle1

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mypage_linearLayout, chickenFragment).commit()
                //현재 사용자가 병아리일 때 해당 Chick fragment로 이동
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mypage_linearLayout, MypageChickFragment()).commit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

}