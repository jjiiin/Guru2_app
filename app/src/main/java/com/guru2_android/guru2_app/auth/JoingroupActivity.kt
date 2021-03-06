package com.guru2_android.guru2_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.guru2_android.guru2_app.R
import com.guru2_android.guru2_app.databinding.ActivityJoingroupBinding

class JoingroupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoingroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_joingroup)
        binding.group1.setOnClickListener {
            val intent= Intent(this, JoinActivity::class.java)
            intent.putExtra("group","1")
            startActivity(intent)
        }
        binding.group2.setOnClickListener {
            val intent= Intent(this, JoinActivity::class.java)
            intent.putExtra("group","2")
            startActivity(intent)
        }

    }
}