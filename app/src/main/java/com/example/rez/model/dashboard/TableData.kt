package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TableData(
    val created_at: String,
    val description: String?,
    val id: Int,
    val image: Int,
    val max_people: Int,
    val price: String,
    val reviews: String?
) : Parcelable