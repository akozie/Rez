package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataXXXX(
    val booked_for: String,
    val booking_reference: String?,
    val confirmed_payment: Boolean,
    val created_at: String,
    val id: String,
    val qr_code: String?,
    val reason: String,
    val status: String,
    val table: String,
    val transactions: Transactions,
    val vendor: String
) : Parcelable

