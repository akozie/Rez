package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Favorite(
    val count: Int,
    val id: Int,
    val state: String
) : Parcelable