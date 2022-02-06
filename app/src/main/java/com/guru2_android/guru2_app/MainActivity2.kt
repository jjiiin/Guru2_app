package com.guru2_android.guru2_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.*
import com.guru2_android.guru2_app.dateDecorator.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.android.synthetic.main.activity_main.materialCalendar
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

    private fun clearSpinner() {
        this.chickListTemp.clear()
    }

    private fun updateSpinner(item: chickModel) {
        this.chickListTemp.add(item)
        Log.d("fun_chick", item.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d("cur_use", Auth.current_email.toString())

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()
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
            .commit()//기본 캘린더 세팅
        //오늘 날짜를 노랑 볼드체로 표시하기
        val todayDecorator = TodayDecorator()
        materialCalendar.addDecorators(todayDecorator)

        //날짜를 string으로 받거나 정제된 날짜형식(0000.00.00)으로 받아오기
        dateText = dayParse(CalendarDay.today())
        dateChange = dayCleanParse(CalendarDay.today())

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
                    }
                }
                //중복 값 지우기
                chickList.clear()
                chickNameList.clear()
                for (data in chickListTemp.distinct()) {
                    chickList.add(data)
                    chickNameList.add(data.name)
                }
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

                        //퀘스트를 리사이클러뷰에 보이기 위한 dataModelList 생성
                        val schRef = database.getReference(chickUID).child(dateText).child("jobs")
                        Log.d("RV_uid",chickUID)
                        schRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                rv.removeAllViewsInLayout()
                                dataModelList.clear()
                                for (DataModel in snapshot.children) {
                                    dataModelList.add(DataModel.getValue(jobModel::class.java)!!)

                                }
                                rvAdapter.notifyDataSetChanged()
                                Log.d("RV_Today",chickUID+dataModelList.toString())

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
                            val schChangeRef = database.getReference(chickUID).child(dateText)
                                .child("jobs")
                            schChangeRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    rv.removeAllViewsInLayout()
                                    dataModelList.clear()
                                    for (DataModel in snapshot.children) {
                                        val item = DataModel.getValue(jobModel::class.java)
                                        dataModelList.add(item!!)
                                    }
                                    rvAdapter.notifyDataSetChanged()
                                    Log.d("RV_change",chickUID+dataModelList.toString())
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

                            var sendUid = chickUID//스피너에 선택된 병아리 uid를 할당함
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

                                                sendUid = item!!.uid
                                                    searchView?.visibility = View.VISIBLE
                                                    searchView?.text = "아이디: ${item!!.email}"

                                                val model = chickModel(item!!.nickname, item!!.uid)
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

                                if (title == "") {  // 타이틀 입력하지 않을 경우 에러 처리
                                    Toast.makeText(this@MainActivity2, "제목을 입력하세요", Toast.LENGTH_SHORT)
                                    Log.d("MainActivity2", "제목 입력하지 않음")
                                } else {
                                    // 푸쉬 알림
                                    val pushRef = database.getReference(sendUid).child("push").child("new")
                                    pushRef.setValue("1")

                                    val schReference =
                                        database.getReference(sendUid).child(dateText).child("jobs")
                                    Log.d("sendUID",sendUid)
                                    var show=false
                                    var count=0
                                    for (i in chickList){
                                        count++
                                        if(sendUid==i.uid){
                                            show=true
                                            break
                                        }
                                    }
                                    if(show){
                                        Log.d("count",count.toString())
                                        count-=1
                                        spinner.setSelection(count)}
                                    val model = jobModel(title, sub, time, image, "", egg)
                                    schReference.child("${title}").setValue(model)
                                    mAlertDialog.dismiss()
                                }
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
                                val myRef = database.getReference(chickUID).child("firm")

                                val model = firmModel(firm, message, dateChange)
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
                //푸쉬알림
                val schRefer =
                    database.getReference(sendUid).child(dateText).child("jobs")
                val model = jobModel(title, sub, time, image, "", egg)
                schRefer.child("${title}").setValue(model)
                mAlertDialog.dismiss()
            }

        }//퀘스트 생성

        //마이페이지
        val myBtn = findViewById<ImageView>(R.id.my)
        myBtn.setOnClickListener {
            //마이페이지로 넘어가기
            var intent = Intent(this, MypageActivity::class.java)
            intent.putParcelableArrayListExtra("list", chickList)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun dayParse(date: CalendarDay): String {
        var parsedDATA: List<String> = date.toString().split("{")
        parsedDATA = parsedDATA[1].split("}").toList()
        parsedDATA = parsedDATA[0].split("-").toList()
        var dateText = "${parsedDATA[0].toInt()}${parsedDATA[1].toInt() + 1}${parsedDATA[2].toInt()}"
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
}