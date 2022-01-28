package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MypageChickenFragment : Fragment() {

    lateinit var text: TextView
    lateinit var settings: ImageView
    lateinit var nickname: String
    lateinit var chick_list: LinearLayout
    lateinit var chicken_message: LinearLayout

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
        settings = view.findViewById(R.id.mypage_chicken_settings)
        chick_list = view.findViewById(R.id.chick_list_layout)
        chicken_message = view.findViewById(R.id.chicken_message_layout)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.value.toString()
                text.text = "${nickname} 님 오늘도 화이팅!"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        // 병아리 목록 activity 이동
        chick_list.setOnClickListener {
            val intent = Intent(getActivity(), ChickListActivity::class.java)
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