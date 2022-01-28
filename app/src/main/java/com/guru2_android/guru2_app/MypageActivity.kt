package com.guru2_android.guru2_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MypageActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        database = Firebase.database.reference
        var uid = Firebase.auth.currentUser?.uid
        var group = database.child("users").child(uid.toString()).child("type").get().toString()

        if (group == "2") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mypage_linearLayout, MypageChickenFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mypage_linearLayout, MypageChickFragment()).commit()
        }

    }

}