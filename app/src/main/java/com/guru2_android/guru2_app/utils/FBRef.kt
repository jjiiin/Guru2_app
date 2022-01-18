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
        val users1Ref = database.getReference("users_group1")
        val users2Ref = database.getReference("users_group2")
        val group1Coll= store.collection("group1")
        val group2Coll= store.collection("group2")

    }



}