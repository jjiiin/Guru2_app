package com.capstone_design.a1209_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Auth {
    companion object {
        //현재 앱에 접속한 사용자의 이메일을 받아옴
        private val auth = Firebase.auth
        val current_email = auth.currentUser!!.email
    }

}