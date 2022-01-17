package com.capstone_design.a1209_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Auth {
    companion object {
        //사용자 uid 얻어오기위함
        private val auth = Firebase.auth

        //사용자 uid얻어옴
        val current_uid = auth.currentUser!!.uid
        val current_email = auth.currentUser!!.email
    }

}