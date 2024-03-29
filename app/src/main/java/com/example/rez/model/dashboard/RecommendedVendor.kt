package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendedVendor(
    val avatar: String,
    val average_rating: String,
    val company_name: String,
    val id: String,
    val category_name: String,
    val total_tables: Int,
    val liked_by_user: Boolean
) : Parcelable