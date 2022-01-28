package com.capstone_design.a1209_app.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        private val database = Firebase.database
        private val store=Firebase.firestore
        //group별 user-database
        val usersRef = database.getReference("users")
        val userSearchRef = database.getReference("userSearch")
        //할 일 전달하는 데베(RDB)
        val jobRef=database.getReference("job")
        //하루 총평(3단계, 칭찬멘트) 전달하는 데베(FDB)
        //val firm=store.collection(//"유저id").document(//"날짜").set("Datamodel(//3단계,칭찬멘트))
    }



}