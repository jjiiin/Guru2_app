package com.guru2_android.guru2_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.MainActivity
import com.guru2_android.guru2_app.MainActivity2
import com.guru2_android.guru2_app.R
import com.guru2_android.guru2_app.dataModel.UserData
import com.guru2_android.guru2_app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_login)

        auth= Firebase.auth

        binding.loginBtn.setOnClickListener {
            //바로 홈 화면이 보여야함.
            val id=binding.idArea.text.toString()
            val pwd=binding.pwdArea.text.toString()

            auth.signInWithEmailAndPassword(id, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //group 확인하고
                        FBRef.usersRef.addValueEventListener(object: ValueEventListener {
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
                    } else {
                        //Toast.makeText(this,"로그인 실패",Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.joinBtn.setOnClickListener{
            startActivity(Intent(this, JoingroupActivity::class.java))
            finish()
        }
    }
    private fun group1(){
        val intent= Intent(this, MainActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun group2(){
        val intent= Intent(this, MainActivity2::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}