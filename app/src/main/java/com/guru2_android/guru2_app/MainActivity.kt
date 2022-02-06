package com.guru2_android.guru2_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.utils.Auth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.guru2_android.guru2_app.dataModel.*
import com.guru2_android.guru2_app.dateDecorator.Day1Decorator
import com.guru2_android.guru2_app.dateDecorator.Day2Decorator
import com.guru2_android.guru2_app.dateDecorator.Day3Decorator
import com.guru2_android.guru2_app.dateDecorator.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    //병아리 회원일 때,
    private lateinit var auth: FirebaseAuth

    private lateinit var dateText: String
    private lateinit var dateChange: String

    private var currentEgg = "0"    // 현재 egg
    private val pickStorage = 1001
    var setImage: Boolean = false
    var questPicture: Uri? = null
    private var imageUri: Uri? = null

    lateinit var mDialogView: View
    val dataModelList = mutableListOf<jobModel>()

    //3단계 평가
    val decoList1 = mutableListOf<CalendarDay>()
    val decoList2 = mutableListOf<CalendarDay>()
    val decoList3 = mutableListOf<CalendarDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("cur_use", Auth.current_email.toString())
        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()

        auth = Firebase.auth
        val database = Firebase.database

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        endTimeCalendar.set(Calendar.MONTH, currentMonth + 3)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // 푸쉬 알림
        var builder = NotificationCompat.Builder(this, "Egg Challenge")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("퀘스트 생성 알림")
            .setContentText("새로운 퀘스트가 추가되었습니다")
            .setContentIntent(pendingIntent)

        val pushRef =
            database.getReference(auth.currentUser?.uid.toString()).child("push").child("new")
        pushRef.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == "1") {    // firebase push 값이 1이면 푸쉬 알림
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 버전 이후에는 알림을 받을 때 채널이 필요
                        val channel_id = "Egg Challenge" // 알림을 받을 채널 id 설정
                        val channel_name = "Egg Challenge" // 채널 이름 설정
                        val descriptionText = "Egg Challenge" // 채널 설명글 설정
                        val importance = NotificationManager.IMPORTANCE_DEFAULT // 알림 우선순위 설정
                        val channel =
                            NotificationChannel(channel_id, channel_name, importance).apply {
                                description = descriptionText
                            }

                        // 만든 채널 정보를 시스템에 등록
                        val notificationManager: NotificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)

                        // 알림 표시: 알림의 고유 ID(ex: 1002), 알림 결과
                        notificationManager.notify(1002, builder.build())
                    }
                    pushRef.setValue("0")   // 푸쉬 알림 후 firebase push 값을 다시 0으로 설정
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("token", token)
        })

        // 현재 egg 가져오기
        FirebaseDatabase.getInstance().reference.child(auth.currentUser?.uid.toString())
            .child("egg").child("totalEgg")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentEgg = snapshot.child("egg").value.toString()
                    if (currentEgg == "null") {    //egg가 없을 경우 0으로 지정
                        currentEgg = "0"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        materialCalendar.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setMinimumDate(CalendarDay.from(currentYear, currentMonth - 1, 1))
            .setMaximumDate(
                CalendarDay.from(
                    currentYear,
                    currentMonth + 3,
                    endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
            )
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        //리싸이클러뷰
        val rv = findViewById<RecyclerView>(R.id.mainRV)
        val rvAdapter = RVAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(this)
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        dateText = dayParse(CalendarDay.today())
        dateChange = dayCleanParse(CalendarDay.today())
        materialCalendar.selectedDate = CalendarDay.today()

        //오늘 날짜를 노랑 볼드체로 표시하기
        val todayDecorator = TodayDecorator()
        materialCalendar.addDecorators(todayDecorator)
        //오늘의 job 가져오기
        val schRef =
            database.getReference(auth.currentUser?.uid.toString()).child(dateText).child("jobs")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rv.removeAllViewsInLayout()
                dataModelList.clear()
                for (DataModel in snapshot.children) {
                    dataModelList.add(DataModel.getValue(jobModel::class.java)!!)
                }
                rvAdapter.notifyDataSetChanged()
                Log.d("DataModel", dataModelList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //날짜 별 3단계 평가 표시하기
        val firmRef = database.getReference(auth.currentUser?.uid.toString()).child("firm")
        firmRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(firmModel::class.java)
                    val dateStr = item!!.date.replace(".", "")
                    val transFormat = SimpleDateFormat("yyyyMMdd")
                    val date = transFormat.parse(dateStr!!)
                    val calDay = CalendarDay.from(date)
                    Log.d("decoList2", calDay.toString())
                    if (item!!.firm != "") {
                        when (item!!.firm.toInt()) {
                            1 -> decoList1.add(calDay)
                            2 -> decoList2.add(calDay)
                            else -> decoList3.add(calDay)
                        }
                    }
                }
                Log.d("decoList2_1", decoList1.toString())
                val dayDeco1 = Day1Decorator(decoList1)
                val dayDeco2 = Day2Decorator(decoList2)
                val dayDeco3 = Day3Decorator(decoList3)
                materialCalendar.addDecorators(dayDeco1, dayDeco2, dayDeco3)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        //날짜 누르는 이벤트-날짜 누르면 그 날의 job을 리싸이클러뷰에 추가하기기
        materialCalendar.setOnDateChangedListener { widget, date, selected ->
            dateText = dayParse(date)
            dateChange = dayCleanParse(date)
            //파이어베이스로부터 job 가져오기
            val schRef = database.getReference(auth.currentUser?.uid.toString()).child(dateText)
                .child("jobs")
            schRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rv.removeAllViewsInLayout()
                    dataModelList.clear()
                    for (DataModel in snapshot.children) {
                        val item = DataModel.getValue(jobModel::class.java)
                        dataModelList.add(item!!)
                    }
                    rvAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
        //리싸이클러뷰 클릭이벤트 - 인증 화면 뜨고 값 받아오기
        rvAdapter.setItemClickListener(object : RVAdapter.OnItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성-alertDialog 만들기(인증 화면)
                val item = dataModelList[position]
                mDialogView =
                    LayoutInflater.from(this@MainActivity).inflate(R.layout.cert_chicken, null)
                val mBuilder = AlertDialog.Builder(this@MainActivity).setView(mDialogView)
                val mAlertDialog = mBuilder.show()


                val title = mAlertDialog.findViewById<TextView>(R.id.title)
                val message = mAlertDialog.findViewById<EditText>(R.id.message)
                val picBtn = mAlertDialog.findViewById<ImageView>(R.id.imgplus)
                val saveBtn = mAlertDialog.findViewById<TextView>(R.id.saveBtn)

                title?.text = item.title

                // 사진 업로드가 필수가 아니면 picBtn 없애기
                if (item.image != "1") {
                    picBtn!!.isVisible = false
                }

                picBtn?.setOnClickListener {
                    pickImage()
                }
                saveBtn?.setOnClickListener {
                    if (item.image == "1" && setImage == false) {   // 사진이 필수인데 이미지를 추가하지 않았을 경우 토스트 메세지 출력
                        Toast.makeText(this@MainActivity, "사진을 추가하세요", Toast.LENGTH_SHORT).show()
                    } else {
                        val certDatabase =
                            database.getReference(auth.currentUser?.uid.toString()).child(dateText)
                                .child("jobs").child(item.title).child("cert")

                        // 이미지를 업로드 했을 경우
                        if (setImage == true) {
                            FirebaseStorage.getInstance().reference.child(auth.currentUser?.uid.toString()) // firebase storage에 이미지 저장
                                .child(dateText).child("jobs")
                                .child(item.title).child("cert").child("image")
                                .putFile(imageUri!!)
                                .addOnSuccessListener {
                                    FirebaseStorage.getInstance().reference.child(auth.currentUser?.uid.toString()) // firebase storage에서 이미지 uri를 가져와 realtime database에 저장
                                        .child(dateText).child("jobs").child(item.title)
                                        .child("cert")
                                        .child("image").downloadUrl.addOnSuccessListener {
                                            questPicture = it
                                            Log.d("tag", "$questPicture")
                                            certDatabase.child("image")
                                                .setValue(questPicture.toString())

                                        }
                                }
                        }

                        //완료 done 키값 바꾸기-색상 변경됨.(RVAdapter)
                        val model =
                            jobModel(item.title, item.sub, item.time, item.image, "1", item.egg)
                        val schRef =
                            database.getReference(auth.currentUser?.uid.toString()).child(dateText)
                                .child("jobs")
                        schRef.child(item.title).setValue(model)

                        //퀘스트 수행 완료 egg내역에 추가(위에랑 같이 움직이기)
                        val eggRef =
                            database.getReference(auth.currentUser?.uid.toString()).child("egg")
                        val eggModel = eggModel(dateChange, item.egg, item.title)
                        eggRef.push().setValue(eggModel)

                        // 퀘스트 수행 완료시 totalegg 추가
                        var changeEgg = 0
                        if (item.egg == "") {   // 퀘스트 생성시 egg를 입력하지 않으면 추가 egg가 없음
                            changeEgg = currentEgg.toInt()
                        } else {
                            changeEgg = currentEgg.toInt() + item.egg.toInt()
                        }

                        Log.d("tag", "currentEgg : ${currentEgg}")
                        Log.d("tag", "item.egg : ${item.egg}")
                        eggRef.child("totalEgg").child("egg").setValue(changeEgg.toString())

                        // 이미지가 없을 때 메세지 저장
                        certDatabase.child("message").setValue(message?.text.toString())
                        setImage = false    // setImage 초기화

                        mAlertDialog.dismiss()
                    }
                }
            }
        })

        //마이페이지
        val myBtn = findViewById<ImageView>(R.id.my)
        myBtn.setOnClickListener {
            var intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }

    }

    private fun dayParse(date: CalendarDay): String {
        var parsedDATA: List<String> = date.toString().split("{")
        parsedDATA = parsedDATA[1].split("}").toList()
        parsedDATA = parsedDATA[0].split("-").toList()
        var dateText =
            "${parsedDATA[0].toInt()}${parsedDATA[1].toInt() + 1}${parsedDATA[2].toInt()}"
        return dateText
    }

    private fun dayCleanParse(date: CalendarDay): String {
        var DATE: String
        var year: String
        var month: String
        var day: String

        var month_tmp = ""
        var day_tmp = ""

        var parsedDATA: List<String> = date.toString().split("{")
        parsedDATA = parsedDATA[1].split("}").toList()
        parsedDATA = parsedDATA[0].split("-").toList()
        year = parsedDATA[0].toInt().toString()
        if (parsedDATA[1].toInt() < 10) {
            var tmp = parsedDATA[1].toInt() + 1
            month_tmp = "0$tmp"
        } else {
            month_tmp = (parsedDATA[1].toInt() + 1).toString()
        }
        month = month_tmp
        if (parsedDATA[2].toInt() < 10) {
            var tmp = parsedDATA[2].toInt()
            day_tmp = "0$tmp"
        } else {
            day_tmp = parsedDATA[2].toInt().toString()
        }
        day = day_tmp
        DATE = "${year}.${month}.${day}"//정제된 날짜
        return DATE
    }

    // 이미지 불러오기
    private fun pickImage() {
        var intent = Intent(Intent.ACTION_PICK) // 갤러리 앱 호출
        intent.type = "image/*"

        startActivityForResult(intent, pickStorage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == pickStorage) {
                val pickedImage: Uri? = data?.data
                if (pickedImage != null) {
                    imageUri = pickedImage
                }
            }

            val complete_picture =
                mDialogView.findViewById<ImageView>(R.id.content)
            Glide.with(this).load(imageUri).into(complete_picture)  // 화면에 출력

            setImage = true // 이미지 업로드 했음을 알림

        }

    }
}