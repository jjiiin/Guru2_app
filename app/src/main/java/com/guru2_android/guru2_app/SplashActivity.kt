package com.guru2_android.guru2_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth= Firebase.auth
        //userid가 존재한다면 바로 main으로 넘어가고 아니면 로그인 화면으로 간다.
        if(auth.currentUser?.uid==null){
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            },3000)

        }else{
            //병아리/닭 회원일 경우 다른 화면이 보여야함.(추후 수정)
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },3000)

        }
    }
}