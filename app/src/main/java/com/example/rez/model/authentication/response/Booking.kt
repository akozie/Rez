package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    val booked_for: String,
    val confirmed_payment: Boolean,
    val created_at: String,
    val id: String,
    val status: String,
    val table: Table,
    val vendor: Vendor
) : Parcelable