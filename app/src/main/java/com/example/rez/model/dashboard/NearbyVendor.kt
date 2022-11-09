package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearbyVendor(
    val avatar: String,
    val company_name: String,
    val distance: Double,
    val id: String,
    val lat: Double,
    val lng: Double,
    val category_name: String,
    val average_rating: String,
    val total_tables: Int,
    val liked_by_user : Boolean
) : Parcelable