package com.guru2_android.guru2_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.MainActivity
import com.guru2_android.guru2_app.MainActivity2
import com.guru2_android.guru2_app.R
import com.guru2_android.guru2_app.dataModel.UserData

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth= Firebase.auth
        val database=Firebase.database
        //userid가 존재한다면 바로 main으로 넘어가고 아니면 로그인 화면으로 간다.
        if(auth.currentUser?.uid==null){
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            },3000)

        }else{
            //병아리/닭 회원일 경우 다른 화면이 보여야함.(추후 수정)
            Handler().postDelayed({
                FBRef.usersRef.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children){
                            val item=data.getValue(UserData::class.java)
                            Log.d("splash",item.toString())
                            if(item!!.uid==auth.currentUser?.uid){
                                if(item!!.group=="1"){
                                    group1()}
                                else{group2()}
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                                  },3000)

        }
    }
    private fun group1(){
        startActivity(Intent(this, MainActivity::class.java))
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
    }
    private fun group2(){
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }
}