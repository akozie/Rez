package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class State(
    val active: Int?,
    val created_at: String?,
    val id: Int?,
    val state: String?,
    val updated_at: String?
) : Parcelable