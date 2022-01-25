package com.guru2_android.guru2_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MypageActivity : AppCompatActivity() {

    lateinit var group: String
    lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val reference: DatabaseReference = database.getReference("users").child(uid.toString()).child("group")

        back = findViewById(R.id.mypage_back)

        back.setOnClickListener {
            finish()
        }

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                group = snapshot.value.toString()

                if (group == "2") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mypage_linearLayout, MypageChickenFragment()).commit()
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