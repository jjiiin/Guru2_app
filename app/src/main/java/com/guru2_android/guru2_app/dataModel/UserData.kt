package com.guru2_android.guru2_app.dataModel

//user가 가입할 때 기본 정보를 담는 데이터 모델
data class UserData(
    val email: String = "",
    val nickname: String = "",
    val group:String="",
    val uid:String=""
)