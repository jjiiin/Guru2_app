package com.guru2_android.guru2_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.MainActivity
import com.guru2_android.guru2_app.MainActivity2
import com.guru2_android.guru2_app.R
import com.guru2_android.guru2_app.dataModel.UserData
import com.guru2_android.guru2_app.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinBinding
    private lateinit var auth: FirebaseAuth
    private var store: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store=Firebase.firestore
        auth= Firebase.auth
        binding= DataBindingUtil.setContentView(this, R.layout.activity_join)
        binding.joinBtn.setOnClickListener {

            var isGotoJoin = true
            val id=binding.idArea.text.toString()
            val pwd=binding.pwdArea.text.toString()
            val pwdCheck=binding.pwdAreaCheck.text.toString()
            val name=binding.nickArea.text.toString()
            val group=intent.getStringExtra("group").toString()

            if(id.isEmpty()){
                Toast.makeText(this,"이메일을 입력해주세요",Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(pwd.isEmpty()){
                Toast.makeText(this,"비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(name.isEmpty()){
                Toast.makeText(this,"닉네임을 입력해주세요", Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(pwd != pwdCheck){
                Toast.makeText(this,"비밀번호를 확인해주세요", Toast.LENGTH_LONG).show()
                isGotoJoin=false
            }
            if(isGotoJoin){
                //신규회원 가입
                Toast.makeText(this,id,Toast.LENGTH_LONG).show()
                auth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"성공",Toast.LENGTH_LONG).show()
                        //회원 데이터베이스에 이메일, 닉네임, 그룹 정보 저장
                        val userData = UserData(id,name,group,auth.currentUser!!.uid)
                        FBRef.usersRef.child(auth.currentUser!!.uid).setValue(userData)//RDB
                        FBRef.userSearchRef.child(name).setValue(userData)
                        //병아리 회원(group1)or 닭회원(group2)별로 보이는 액티비티 나누기(추후)
                        //병아리 회원일 때,
                        if(group=="1"){
                            val intent= Intent(this, MainActivity::class.java)
                            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            }else{ //닭회원일 때(group="2")
                            val intent= Intent(this, MainActivity2::class.java)
                            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        }

                    } else {
                        Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}