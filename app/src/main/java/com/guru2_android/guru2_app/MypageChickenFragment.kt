package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.auth.LoginActivity
import java.util.ArrayList

class MypageChickenFragment : Fragment() {

    lateinit var text: TextView
    lateinit var nickname: String
    lateinit var chick_list: LinearLayout
    lateinit var chicken_message: LinearLayout
    lateinit var logout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val reference: DatabaseReference =
            database.getReference("users").child(uid.toString()).child("nickname")
        val view = inflater.inflate(R.layout.fragment_mypage_chicken, container, false)

        text = view.findViewById(R.id.mypage_chicken_text)
        chick_list = view.findViewById(R.id.chick_list_layout)
        chicken_message = view.findViewById(R.id.chicken_message_layout)
        logout = view.findViewById(R.id.mypage_chicken_logout)

        // 닭 닉네임 출력
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.value.toString()
                text.text = "${nickname} 님 오늘도 화이팅!"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        // 로그아웃 버튼 클릭
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 닭의 병아리 리스트 받아오기
        val chickList = arguments?.getSerializable("list")

        // 병아리 목록 activity 이동
        chick_list.setOnClickListener {
            val intent = Intent(getActivity(), ChickListActivity::class.java)
            intent.putParcelableArrayListExtra("list", chickList as ArrayList<out Parcelable>?)
            startActivity(intent)
        }

        // 내가 남긴 칭찬 메세지 activity 이동
        chicken_message.setOnClickListener {
            val intent = Intent(getActivity(), LastMessageChickenActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}