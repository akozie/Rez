package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuggestedVendor(
    val avatar: String,
    val average_rating: Float,
    val company_name: String,
    val id: Int,
    val category_name: String,
    val total_tables: Int,
    val liked_by_user: Boolean
) : Parcelable