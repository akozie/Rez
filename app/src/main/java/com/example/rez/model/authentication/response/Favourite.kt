package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favourite(
    val avatar: String,
    val company_name: String,
    val id: String,
    val ratings: String,
    val state: String
) : Parcelable