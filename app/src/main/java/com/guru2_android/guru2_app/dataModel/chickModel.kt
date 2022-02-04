package com.guru2_android.guru2_app.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//병아리의 uid, nickname 저장하는 데이터모델
//배열로 MyPageActivity에 데이터를 전달함-Parcelize
@Parcelize
data class chickModel(
    val name:String="",
    val uid:String=""
): Parcelable
