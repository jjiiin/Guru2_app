package com.guru2_android.guru2_app.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class chickModel(
    val name:String="",
    val uid:String=""
): Parcelable
