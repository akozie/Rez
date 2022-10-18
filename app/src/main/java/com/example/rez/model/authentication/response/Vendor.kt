package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vendor(
    var id: String?,
    var name: String?
) : Parcelable