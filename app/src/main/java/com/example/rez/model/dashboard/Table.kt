package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Table(
    val created_at: String,
    val description: String,
    val id: String,
    val image: String?,
    val max_people: Int,
    val price: String,
    val quantity: Int,
    val status: Boolean,
    val name: String
) : Parcelable
