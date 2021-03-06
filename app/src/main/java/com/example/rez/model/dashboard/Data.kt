package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val address: String,
    val avatar: String,
    val category: String,
    val company_name: String,
    val contact_name: String,
    val description: String,
    val images: List<Image>,
    val lat: Double,
    val lga: String,
    val lng: Double,
    val opened: Boolean,
    val ratings: String,
    val state: String,
    val tables: List<Table>
) : Parcelable