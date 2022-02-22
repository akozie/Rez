package com.example.rez.model.authentication.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Table(
    var id: Int,
    var name: String,
    var table_image_url: String
) : Parcelable