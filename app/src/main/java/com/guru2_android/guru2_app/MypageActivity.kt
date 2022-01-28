package com.guru2_android.guru2_app

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

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

        //뒤로가기 버튼
        back = findViewById(R.id.mypage_back)

        back.setOnClickListener {
            finish()
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                group = snapshot.value.toString()

                // 현재 사용자가 chicken일 때 해당 fragment로 이동
                if (group == "2") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mypage_linearLayout, MypageChickenFragment()).commit()
                //현재 사용자가 chick일 때 해당 fragment로 이동
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