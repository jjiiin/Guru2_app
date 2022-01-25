package com.guru2_android.guru2_app

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.guru2_android.guru2_app.databinding.ActivityMainBinding
import com.guru2_android.guru2_app.databinding.QuestCompleteDialogBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()
    private lateinit var chickenUid: String

    private lateinit var binding: ActivityMainBinding
    private val PICK_STORAGE = 1001
    private var imageUri: Uri? = null

    lateinit var completeDialog: View
    lateinit var builder: AlertDialog.Builder
    lateinit var time: String
    var setImage: Boolean = false
    var questPicture: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            var intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button3.setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val data2 = data.child("child")
                        if (data2.value != null) {
                            for (data3 in data2.children) {
                                if (data3.key == uid) {
                                    chickenUid = data.key.toString()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            completeDialog = LayoutInflater.from(this).inflate(R.layout.quest_complete_dialog, null)
            builder = AlertDialog.Builder(this).setView(completeDialog)

            val dialog = builder.show()

            val picture_btn =
                completeDialog.findViewById<ImageView>(R.id.quest_complete_qicture_plus)
            val complete_btn = completeDialog.findViewById<Button>(R.id.quest_complete_btn)
            val completeText = completeDialog.findViewById<EditText>(R.id.quest_complete_text)

            picture_btn.setOnClickListener {
                pickImage()
            }

            complete_btn.setOnClickListener {
                Log.d("tag", "setImage ${setImage}")
                Log.d("tag", "수행 완료 버튼 클릭")

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                time = current.format(formatter)
                Log.d("tag", "$time")

                val certDatabase =
                    FirebaseDatabase.getInstance().reference.child("quest")
                        .child(uid)
                        .child(chickenUid).child(time).child("job").child("3")
                        .child("cert")
                if (setImage == true) {
                    FirebaseStorage.getInstance().reference.child("quest").child(uid).child(chickenUid)
                        .child(time).child("job").child("3").putFile(imageUri!!).addOnSuccessListener {

                            FirebaseStorage.getInstance().reference.child("quest").child(uid)
                                .child(chickenUid).child(time).child("job")
                                .child("3").downloadUrl.addOnSuccessListener {
                                    questPicture = it
                                    Log.d("tag", "$questPicture")
                                    certDatabase.child("image").setValue(questPicture.toString())

                                }

                        }
                }

                certDatabase.child("message").setValue(completeText.text.toString())

                dialog.dismiss()
            }
        }

    }

    private fun pickImage() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, PICK_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_STORAGE) {
                val pickedImage: Uri? = data?.data
                if (pickedImage != null) {
                    imageUri = pickedImage
                }
            }

            val complete_picture =
                completeDialog.findViewById<ImageView>(R.id.quest_complete_picture)
            Glide.with(this).load(imageUri).into(complete_picture)

            setImage = true

        }

    }

}
