package com.example.rez.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NearRestaurantData(
    val restaurantName: String? = null,
    val restaurantImage: Int? = null,
    val rating: String? = null,
    val categoryName: String? = null,
    val tables: String? = null,
    val distance: String? = null,
    val address: String? = null,
    val restaurantId: String? = null,
    val liked: String? = null,
    val totalReviews: String? = null

) : Parcelable