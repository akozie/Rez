package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val description: String,
    val id: String,
    val image_url: String
) : Parcelable