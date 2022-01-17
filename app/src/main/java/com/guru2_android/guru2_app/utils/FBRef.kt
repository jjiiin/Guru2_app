package com.capstone_design.a1209_app.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        private val database = Firebase.database
        //group별 user-database
        val users1Ref = database.getReference("users_group1")
        val users2Ref = database.getReference("users_group2")


    }



}