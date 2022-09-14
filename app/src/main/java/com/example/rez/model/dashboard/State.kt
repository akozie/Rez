package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class State(
    val active: Int?,
    val country_id: Int?,
    val flag: Int?,
    val created_at: String?,
    val id: Int?,
    val updated_at: String?,
    val name: String?,
    val country_code: String?,
    val fips_code: String?,
    val iso2: String?,
    val latitude: String?,
    val longitude: String?,
    val wikiDataId: String?,
) : Parcelable

