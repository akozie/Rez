package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataXXXXXXX(
    val friday: List<String?>?,
    val monday: List<String?>,
    val saturday: List<String?>?,
    val sunday: List<String?>?,
    val thursday: List<String?>?,
    val tuesday: List<String?>?,
    val wednesday: List<String?>?
) : Parcelable