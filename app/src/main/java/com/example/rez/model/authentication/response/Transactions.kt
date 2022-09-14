package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transactions(
    val amount: String,
    val method: String,
    val reference: String?,
    val status: String?
) : Parcelable
