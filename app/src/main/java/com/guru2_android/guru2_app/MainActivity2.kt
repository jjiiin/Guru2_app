package com.guru2_android.guru2_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone_design.a1209_app.utils.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.*
import com.guru2_android.guru2_app.dateDecorator.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.materialCalendar
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class MainActivity2 : AppCompatActivity() {
    //닭회원일 때
    private lateinit var auth: FirebaseAuth
    private lateinit var dateText: String
    private lateinit var dateChange: String

    private var currentEgg = "0"    // 현재 egg
    lateinit var mCheckDialogView: View
    val dataModelList = mutableListOf<jobModel>()
    val chickList = arrayListOf<chickModel>()
    val chickNameList = arrayListOf<String>()
    val chickListTemp = arrayListOf<chickModel>()
    val chickNameListTemp = arrayListOf<String>()
    private fun clearSpinner() {
        this.chickListTemp.clear()
        this.chickNameListTemp.clear()
    }

    private fun updateSpinner(item: chickModel) {
        this.chickListTemp.add(item)
        Log.d("fun_chick", item.toString())
    }

    private fun updateNameSpinner(name: String) {
        this.chickNameListTemp.add(name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d("cur_use", Auth.current_email.toString())

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()
        var count = 1
        var chickUID = ""

        val rv = findViewById<RecyclerView>(R.id.mainRV)
        val rvAdapter = RVAdapter(dataModelList)
        rv.adapter = rvAdapter
        val layout = LinearLayoutManager(this@MainActivity2)
        rv.layoutManager = layout
        rv.setHasFixedSize(true)

        auth = Firebase.auth

        val database = Firebase.database

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)
        endTimeCalendar.set(Calendar.MONTH, currentMonth + 3)

        materialCalendar.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setMinimumDate(CalendarDay.from(currentYear,currentMonth-1, 1))
            .setMaximumDate(
                CalendarDay.from(
                    currentYear,
                    currentMonth + 3,
                    endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
            )
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        //스피너에 chick list만들기
        val chickRef = database.getReference(auth.currentUser?.uid.toString()).child("chick")
        chickRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearSpinner()
                for (data in snapshot.children) {
                    val item = data.getValue(chickModel::class.java)
                    Log.d("item_spinner", item.toString())
                    if (item != null) {
                        updateSpinner(item)
                        updateNameSpinner(item.name)
                    }
                }
                //중복 값 지우기
                chickList.clear()
                chickNameList.clear()
                for (data in chickListTemp.distinct()) {
                    chickList.add(data)
                    chickNameList.add(data.name)
                }

                //오늘 날짜를 노랑 볼드체로 표시하기
                val todayDecorator = TodayDecorator(this@MainActivity2)
                materialCalendar.addDecorators(todayDecorator)

                dateText = dayParse(CalendarDay.today())
                dateChange = dayCleanParse(CalendarDay.today())

                //스피너 구현
                val spinner: Spinner = findViewById(R.id.spinner)
                val adapter = ArrayAdapter(
                    this@MainActivity2,
                    android.R.layout.simple_spinner_dropdown_item,
                    chickNameList
                )
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        chickUID = chickList[position].uid
                        //오늘의 job 가져오기-getReference를 spinner에 있는 uid로 바꾸기
//                        dateText = dayParse(CalendarDay.today())
//                        dateChange = dayCleanParse(CalendarDay.today())

                        // 현재 egg 가져오기
                        FirebaseDatabase.getInstance().reference.child(chickUID).child("egg").child("totalEgg")
                            .addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    currentEgg = snapshot.child("egg").value.toString()
                                    Log.d("tag", "currentEgg : ${currentEgg}")
                                    if (currentEgg == "null") {    //egg가 없을 경우 0으로 지정
                                        currentEgg = "0"
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })

                        val schRef = database.getReference(chickUID).child(dateText).child("jobs")
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
                        //날짜 누르는 이벤트-날짜 누르면 그 날의 job을 리싸이클러뷰에 추가하기기
                        materialCalendar.setOnDateChangedListener { widget, date, selected ->
                            dateText = dayParse(date)
                            dateChange = dayCleanParse(date)
                            //파이어베이스로부터 job 가져오기
                            val schRef = database.getReference(chickUID).child(dateText)
                                .child("jobs")
                            schRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    rv.removeAllViewsInLayout()
                                    dataModelList.clear()
                                    //itemModelList.clear()
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
                        }//날짜 바뀔 때마다 리싸이클러 뷰 바꾸기
                        //리사이클러뷰 클릭 리스너
                        rvAdapter.setItemClickListener(object : RVAdapter.OnItemClickListener {
                            override fun onClick(v: View, position: Int) {
                                // 클릭 시 이벤트 작성-alertDialog 만들기(인증 화면 확인)
                                val item = dataModelList[position]
                                mCheckDialogView = LayoutInflater.from(this@MainActivity2)
                                    .inflate(R.layout.check_dialog, null)
                                val mBuilder = AlertDialog.Builder(this@MainActivity2)
                                    .setView(mCheckDialogView)
                                val mAlertDialog = mBuilder.show()
                                lateinit var imageUri: String

                                val title = mAlertDialog.findViewById<TextView>(R.id.title)
                                val message = mAlertDialog.findViewById<TextView>(R.id.message)
                                val image = mAlertDialog.findViewById<ImageView>(R.id.content)
                                Log.d("clicked", item.title)
                                //Toast.makeText(this@MainActivity2,item.title,Toast.LENGTH_LONG).show()
                                title!!.text = item.title
                                FirebaseDatabase.getInstance().reference.child(chickUID)
                                    .child(dateText)
                                    .child("jobs").child(item.title)
                                    .child("cert").addValueEventListener(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (data2 in snapshot.children) {
                                                if (data2.key == "image") {
                                                    imageUri = data2.getValue().toString()
                                                    Log.d("tag", "imageUri : $imageUri")
                                                    if (imageUri != null) {    // image가 있으면 image 출력
                                                        Glide.with(this@MainActivity2)
                                                            .load(imageUri).into(image!!)
                                                    }
                                                }
                                                if (data2.key == "message") {
                                                    message?.text = data2.getValue().toString()
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                val saveBtn = mAlertDialog.findViewById<TextView>(R.id.saveBtn)
                                saveBtn?.setOnClickListener {
                                    mAlertDialog.dismiss()
                                }

                            }
                        })//리사이클러뷰 클릭 리스너

                        //퀘스트 생성
                        val plusBtn = findViewById<ImageView>(R.id.plus)
                        plusBtn.setOnClickListener {
                            val mDialogView = LayoutInflater.from(this@MainActivity2)
                                .inflate(R.layout.input_dialog, null)
                            val mBuilder =
                                AlertDialog.Builder(this@MainActivity2).setView(mDialogView)
                            val mAlertDialog = mBuilder.show()

                            var sendUid = chickUID
                            val searchBtn = mAlertDialog.findViewById<ImageView>(R.id.searchBtn)
                            searchBtn?.setOnClickListener {
                                val search =
                                    mAlertDialog.findViewById<EditText>(R.id.search)?.text.toString()
                                val database = Firebase.database
                                val searchRef = database.getReference("users")
                                searchRef.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (data in snapshot.children) {
                                            val item = data.getValue(UserData::class.java)
                                            if (item!!.nickname == search) {
                                                val searchView =
                                                    mAlertDialog.findViewById<TextView>(R.id.emailView)
                                                searchView?.visibility = View.VISIBLE
                                                searchView?.text = "아이디: ${item!!.email}"
                                                sendUid = item!!.uid
                                                val model = chickModel(item!!.nickname, item!!.uid)
                                                //val chickModel=chickNameModel(item!!.nickname)
                                                val chickRef =
                                                    database.getReference(auth.currentUser?.uid.toString())
                                                        .child("chick")
                                                chickRef.push().setValue(model)
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                            //이미지 체크
                            var image = ""
                            val imageCheck = mAlertDialog.findViewById<CheckBox>(R.id.imageCheck)
                            imageCheck?.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked) {
                                    image = "1"
                                }
                            }
                            var day = ""
                            val amBtn = mAlertDialog.findViewById<Button>(R.id.am)
                            amBtn?.setOnClickListener {
                                day = "AM"
                            }
                            val pmBtn = mAlertDialog.findViewById<Button>(R.id.pm)
                            pmBtn?.setOnClickListener {
                                day = "PM"
                            }

                            Log.d("sendUid", sendUid)
                            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
                            saveBtn?.setOnClickListener {
                                val title =
                                    mAlertDialog.findViewById<EditText>(R.id.title)?.text.toString()
                                val sub =
                                    mAlertDialog.findViewById<EditText>(R.id.sub)?.text.toString()

                                //시간 설정
                                var time = ""
                                var hour =
                                    mAlertDialog.findViewById<EditText>(R.id.hour)?.text.toString()
                                if (hour != "" && hour.toInt() < 10) {    // hour이 선택되었을 경우
                                    hour = "0${hour}"
                                }
                                var minute =
                                    mAlertDialog.findViewById<EditText>(R.id.min)?.text.toString()
                                if (minute != "" && minute.toInt() < 10) {  // minute이 선택되었을 경우
                                    minute = "0${minute}"
                                }

                                if (hour != "" && minute != "") {   // hour, minute이 선택되었을 경우
                                    time = "${day} ${hour}:${minute}"
                                } else {    // hour, minute이 선택되지 않았을 경우
                                    time = ""
                                }

                                val egg =
                                    mAlertDialog.findViewById<EditText>(R.id.egg)?.text.toString()
                                val database = Firebase.database

                                Log.e("DateText", dateText)

                                // 푸쉬 알림
                                val pushRef = database.getReference(sendUid).child("push").child("new")
                                pushRef.setValue("1")

                                //앞에서 더 밀려 나와야할 듯. uid로 db이름 하기
                                val schRef =
                                    database.getReference(sendUid).child(dateText).child("jobs")
                                val model = jobModel(title, sub, time, image, "", egg)
                                schRef.child("${title}").setValue(model)
                                mAlertDialog.dismiss()
                            }

                        }//퀘스트 생성

                        //최종확인(체크 버튼)-닭
                        val firmBtn = findViewById<ImageView>(R.id.firm)
                        firmBtn.setOnClickListener {
                            val mDialogView = LayoutInflater.from(this@MainActivity2)
                                .inflate(R.layout.firm_dialog, null)
                            val mBuilder =
                                AlertDialog.Builder(this@MainActivity2).setView(mDialogView)
                            val mAlertDialog = mBuilder.show()

                            var firm = ""
                            val badBtn = mAlertDialog.findViewById<ImageView>(R.id.bad)
                            badBtn?.setOnClickListener {
                                firm = "1"
                            }
                            val goodBtn = mAlertDialog.findViewById<ImageView>(R.id.good)
                            goodBtn?.setOnClickListener {
                                firm = "2"
                            }
                            val greatBtn = mAlertDialog.findViewById<ImageView>(R.id.great)
                            greatBtn?.setOnClickListener {
                                firm = "3"
                            }
                            val saveBtn = mAlertDialog.findViewById<TextView>(R.id.saveBtn)
                            saveBtn?.setOnClickListener {

                                val message =
                                    mAlertDialog.findViewById<EditText>(R.id.message)?.text.toString()
                                val database = Firebase.database
                                auth = Firebase.auth
                                //레퍼런스에 병아리 uid가져와야함.

                                val myRef = database.getReference(chickUID).child("firm")
                                //앞에서 더 밀려 나와야할 듯. uid로 db이름 하기

                                val model = firmModel(firm, message, dateChange)
                                //Log.d("Firmday",selectedDay.toString())
                                myRef.push().setValue(model)

                                //good일 경우 10egg추가하기
                                if (firm == "3") {
                                    val eggRef = database.getReference(chickUID).child("egg")
                                    val eggModel = eggModel(dateChange, "10", "Bonus EGG")
                                    eggRef.push().setValue(eggModel)

                                    // total egg 수정
                                    val changeEgg = currentEgg.toInt() + 10
                                    Log.d("tag", "currentEgg: ${currentEgg}")
                                    eggRef.child("totalEgg").child("egg").setValue(changeEgg.toString())
                                }
                                val messRef = database.getReference(chickUID).child("message")
                                val messchickRef =
                                    database.getReference(auth.currentUser!!.uid).child("message")
                                val messModel = messageModel(dateChange, message)
                                val messChickModel =
                                    messChicken(dateChange, chickList[position].name, message)
                                //병아리의 데이터베이스에 최종 평가 메세지 저장
                                messRef.push().setValue(messModel)
                                //닭의 데이터베이스에 최종 평가 메세지 저장
                                messchickRef.push().setValue(messChickModel)
                                mAlertDialog.dismiss()
                            }
                        }//최종확인
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //스피너에 병아리 리스트 없을 때 퀘스트 생성
        val plusBtn = findViewById<ImageView>(R.id.plus)
        plusBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this@MainActivity2)
                .inflate(R.layout.input_dialog, null)
            val mBuilder =
                AlertDialog.Builder(this@MainActivity2).setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            var sendUid = ""
            val searchBtn = mAlertDialog.findViewById<ImageView>(R.id.searchBtn)
            searchBtn?.setOnClickListener {
                val search =
                    mAlertDialog.findViewById<EditText>(R.id.search)?.text.toString()
                val database = Firebase.database
                val searchRef = database.getReference("users")
                searchRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val item = data.getValue(UserData::class.java)
                            if (item!!.nickname == search) {
                                val searchView =
                                    mAlertDialog.findViewById<TextView>(R.id.emailView)
                                searchView?.visibility = View.VISIBLE
                                searchView?.text = "아이디: ${item!!.email}"
                                sendUid = item!!.uid
                                val model = chickModel(item!!.nickname, item!!.uid)
                                //val chickModel=chickNameModel(item!!.nickname)
                                val chickRef =
                                    database.getReference(auth.currentUser?.uid.toString())
                                        .child("chick")
                                chickRef.push().setValue(model)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            //이미지 체크
            var image = ""
            val imageCheck = mAlertDialog.findViewById<CheckBox>(R.id.imageCheck)
            imageCheck?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    image = "1"
                }
            }
            var day = ""
            val amBtn = mAlertDialog.findViewById<Button>(R.id.am)
            amBtn?.setOnClickListener {
                day = "AM"
            }
            val pmBtn = mAlertDialog.findViewById<Button>(R.id.pm)
            pmBtn?.setOnClickListener {
                day = "PM"
            }

            Log.d("sendUid", sendUid)
            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn?.setOnClickListener {
                val title =
                    mAlertDialog.findViewById<EditText>(R.id.title)?.text.toString()
                val sub =
                    mAlertDialog.findViewById<EditText>(R.id.sub)?.text.toString()

                //시간 설정
                var time = ""
                var hour =
                    mAlertDialog.findViewById<EditText>(R.id.hour)?.text.toString()
                if (hour.toInt() < 10) {
                    hour = "0${hour}"
                }
                var minute =
                    mAlertDialog.findViewById<EditText>(R.id.min)?.text.toString()
                if (minute.toInt() < 10) {
                    minute = "0${minute}"
                }
                time = "${day} ${hour}:${minute}"

                val egg =
                    mAlertDialog.findViewById<EditText>(R.id.egg)?.text.toString()
                val database = Firebase.database

                Log.e("DateText", dateText)
                //푸쉬알림
                //앞에서 더 밀려 나와야할 듯. uid로 db이름 하기
                val schRef =
                    database.getReference(sendUid).child(dateText).child("jobs")
                val model = jobModel(title, sub, time, image, "", egg)
                schRef.child("${title}").setValue(model)
                mAlertDialog.dismiss()
            }

        }//퀘스트 생성


        //마이페이지
        val myBtn = findViewById<ImageView>(R.id.my)
        myBtn.setOnClickListener {
            //마이페이지로 넘어가기
            var intent = Intent(this, MypageActivity::class.java)
            intent.putParcelableArrayListExtra("list", chickList)
            Log.d("MainActivity2 tag", "chickList ${chickList}")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun dayParse(date: CalendarDay): String {
        var month: String
        var day: String
        //var dateText:String
        var selectedDay: CalendarDay

        var DATE: String
        var year: String
        var month_tmp = ""
        var day_tmp = ""

        selectedDay = date
        DATE = selectedDay.toString()
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
            var tmp = parsedDATA[2].toInt() + 1
            day_tmp = "0$tmp"
        } else {
            day_tmp = parsedDATA[2].toInt().toString()
        }
        day = day_tmp
        Log.e("Date_DATE", DATE)
        var dateText = "${year}${parsedDATA[1].toInt() + 1}${parsedDATA[2].toInt()}"
        DATE = "${year}.${month}.${day}"//정제된 날짜
        return dateText
    }

    private fun dayCleanParse(date: CalendarDay): String {
        var month: String
        var day: String
        //var dateText:String
        var selectedDay: CalendarDay

        var DATE: String
        var year: String
        var month_tmp = ""
        var day_tmp = ""

        selectedDay = date
        DATE = selectedDay.toString()
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
        Log.e("Date_DATE", DATE)
        //dateText="${year}${parsedDATA[1].toInt()+1}${parsedDATA[2].toInt()}"
        DATE = "${year}.${month}.${day}"//정제된 날짜
        return DATE
    }

    // 푸쉬 알림
//    private fun createNotificationChannel(builder: NotificationCompat.Builder, notificationId: Int) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.app_name)
//            val descriptionText = "테스트"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("Egg", name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//
//            notificationManager.notify(notificationId, builder.build())
//
//            Log.d("tag", "푸쉬 알림")
//        }
//    }
}