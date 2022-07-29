package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchModel(
    var searchText: String?,
    val typeID: Int?
) : Parcelable
