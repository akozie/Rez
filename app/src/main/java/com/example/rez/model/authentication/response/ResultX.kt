package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultX(
    val avatar: String,
    val company_name: String,
    val id: String,
    val ratings: Int
) : Parcelable