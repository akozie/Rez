package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    var booked_for: String,
    var confirmed_payment: Boolean,
    val created_at: String,
    var id: Int,
    var status: String,
    var table: Table,
    val vendor: Vendor
) : Parcelable