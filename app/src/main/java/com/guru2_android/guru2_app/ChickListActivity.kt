package com.guru2_android.guru2_app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.chickModel

class ChickListActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()

    //private var info: ArrayList<ChickInfo> = arrayListOf()
    //private var pointArray: ArrayList<String> = arrayListOf()
    //private var chickModel: ArrayList<chickModel> = arrayListOf()
    //lateinit var chickUid: String
    lateinit var chick_list_back: ImageView
    private var chickList: ArrayList<chickModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chick_list)

        // 뒤로 가기 버튼
        chick_list_back = findViewById(R.id.chick_list_back)





        // 닭의 child인 병아리의 point 정보를 pointArray 배열에 담음
//        for (chickData in chickModel) {
//            FirebaseDatabase.getInstance().reference.child(chickData.uid).child("egg").child("totalEgg")
//                .addValueEventListener(object :
//                    ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        var point = snapshot.child("egg").value.toString()
//                        if (point == "null") {  // point가 생성되지 않았을 경우 0으로 지정
//                            point = "0"
//                        }
//                        pointArray.add(point!!)
//                        Log.d("tag", "init pointArray ${pointArray}")
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//
//                })
//        }

        val recyclerView = findViewById<RecyclerView>(R.id.chick_list_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()

        // 뒤로가기 버튼 클릭시 finish
        chick_list_back.setOnClickListener {
            finish()
        }
    }

    // 병아리 목록 recycler view로 출력
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            // 병아리 리스트 가져오기
            chickList = intent.getSerializableExtra("list") as ArrayList<chickModel>

            // chicken의 child인 병아리 정보를 info 배열에 담음
//            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("child")
//                .addValueEventListener(object :
//                    ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (data in snapshot.children) {
//                            val data2 = data.child("info")
//                            val item = data2.getValue<ChickInfo>()
//                            info.add(item!!)
//                            chickUid = data.key.toString()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//
//                })
        }

        // item_chick_list로 view holder 생성
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CustomViewHolder {
            val inflatedView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chick_list, parent, false)
            return CustomViewHolder(inflatedView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textName: TextView = itemView.findViewById(R.id.chick_list_name)
//            val textPoint: TextView = itemView.findViewById(R.id.chick_list_point)
        }

        // holder에 info 배열에 저장된 값을 넣음
        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Log.d("tag", "chickList : ${chickList}")
            holder.textName.text = chickList[position].name

            // item을 클릭하면 chick의 egg를 차감하는 페이지로 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(this@ChickListActivity, ChickenEggActivity::class.java)
                intent.putExtra("chickUid", chickList[position].uid)   // intent에 chickUid를 같이 보냄
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return chickList.size
        }

    }
}