package com.guru2_android.guru2_app

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.w3c.dom.Text


class Quest_create : AppCompatActivity() {

    lateinit var subject_edit : EditText

    lateinit var content_edit : EditText
    lateinit var send_quest : EditText
    lateinit var time_check : CheckBox
    lateinit var time_picker : TimePicker
    lateinit var photo_check : CheckBox
    lateinit var egg_check : CheckBox
    lateinit var finish_btn : Button
    lateinit var prize_edit : EditText
    lateinit var prize_text : TextView
    lateinit var prize_text2 : TextView
    lateinit var prize_db : String
    lateinit var subject_db : String
    lateinit var content_db : String
    lateinit var send_db : String
    lateinit var alarm_edit : EditText
    lateinit var alarm_check : CheckBox
    lateinit var alarm_text : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "퀘스트 작성"


        /////////////////////////////변수 연결//////////////////////////
        subject_edit = findViewById(R.id.subject_edit)
        content_edit = findViewById(R.id.content_edit)
        send_quest = findViewById(R.id.send_edit)
        time_check = findViewById(R.id.time_check)
        time_picker = findViewById(R.id.time_picker)
        photo_check = findViewById(R.id.photo_check)
        egg_check = findViewById(R.id.egg_check)
        prize_edit = findViewById(R.id.prize_edit)
        prize_text = findViewById(R.id.prize_text)
        prize_text2 = findViewById(R.id.prize_text2)
        finish_btn = findViewById(R.id.finish_btn)
        alarm_check = findViewById(R.id.alarm_check)
        alarm_edit = findViewById(R.id.alarm_edit)
        alarm_text = findViewById(R.id.alarm_text)


        ////////////////////////////////////////////////////////////////



        ///////////(1) edit 부분 ///////////////////////////////////
        //1. 제목 입력한 것을 데이터베이스에 보내기

        subject_db = subject_edit.text.toString()


        //2. 내용 데이터베이스로 보내기
        content_db = subject_edit.text.toString()

        //3. '퀘스트 보내기' 데이터베이스로
        send_db = send_quest.text.toString()

        ///////////////////////////////////////////////////////////


        ///////////////////////(2) checkbox 부분////////////////////////
        //1. 시간(선택) 체크하면 타임피커 뜸
        time_check.setOnCheckedChangeListener{buttonView, isChecked ->
            if(time_check.isChecked == true) {
                time_picker.visibility = android.view.View.VISIBLE
            }
            else{
                time_picker.visibility = android.view.View.GONE
            }
        }



        //2. 타임피커에서 선택 된 시간 값 가져오기


        //3. 알림 설정(선택) 체크하면 분 입력 뜸
        alarm_check.setOnCheckedChangeListener{buttonView, isChecked ->
            if(alarm_check.isChecked == true) {
                alarm_edit.visibility = android.view.View.VISIBLE
                alarm_text.visibility = android.view.View.VISIBLE
            }
            else{
                alarm_edit.visibility = android.view.View.GONE
                alarm_text.visibility = android.view.View.GONE
            }
        }

        //4. 분 입력한 것을 데이터 베이스로 보내기



        //5. 사진 인증을 체크했을 때 병아리의 퀘스트 수행 완료 부분에서 사진등록 뜨게 하기
        photo_check.setOnCheckedChangeListener{buttonView, isChecked->
            var photo_true : Int =0
            if(photo_check.isChecked ==true){
                photo_true =1
            }

        }



        //6. Egg 지급을 체크했을 때 얼마나 egg를 줄건지 텍스트 뷰 뜨게 하기

        egg_check.setOnCheckedChangeListener{buttonView, isChecked ->
            if(egg_check.isChecked == true) {
                prize_text.visibility = android.view.View.VISIBLE
                prize_edit.visibility = android.view.View.VISIBLE
                prize_text2.visibility= android.view.View.VISIBLE
            }
            else{
                prize_text.visibility = android.view.View.GONE
                prize_edit.visibility = android.view.View.GONE
                prize_text2.visibility= android.view.View.GONE
            }
        }

        //7. 입력한 egg만큼 데이터 베이스로 보내기


        prize_db = prize_edit.text.toString()






        ///////////////////////////////////////////////////////////

        //////////////////////(3) button 부분/////////////////////////
        /*finish_btn.setOnClickListener{
            var intent = Intent(this, 보낼 액티비티 이름 :: class.java)
            intent.putExtra("subject", subject_edit.text.toString())
            intent.putExtra("content", content_edit.text.toString())
            intent.putExtra("send_quest", send_quest.text.toString())
            intent.putExtra("time_picker", time_picker.text.toString()) (???)
            intent.putExtra("photo_check", photo_check.text.toString())
            intent.putExtra("prize_edit", prize_edit.text.toString())

        }
        */
        //////////////////////////////////////////////////////////





    }

}
