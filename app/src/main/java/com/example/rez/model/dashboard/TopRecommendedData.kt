package com.example.rez.model.dashboard

data class TopRecommendedData (
     val restaurantId: String,
     val restaurantName: String? = null,
     val restaurantImage: Int? = null,
     val rating: String,
     val totalReviews: String? = null,
     val categoryName: String? = null,
     val tables: String? = null,
     val distance: String? = null,
     val address: String? = null
)