package com.capstone_design.a1209_app.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        private val database = Firebase.database
        //group별 user-database
        val usersRef = database.getReference("users")
        //퀘스트를 생성할 때 유저를 검색하기 위해 레퍼런스를 받아오고 변수에 할당함
        val userSearchRef = database.getReference("userSearch")

    }



}