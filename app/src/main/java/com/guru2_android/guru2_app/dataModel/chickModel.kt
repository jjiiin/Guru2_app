package com.guru2_android.guru2_app.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//병아리의 uid, nickname 저장하는 데이터모델
@Parcelize
data class chickModel(
    val name:String="",
    val uid:String=""
): Parcelable
