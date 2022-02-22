package com.example.rez.model.dashboard

data class DataX(
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val max_people: Int,
    val name: String,
    val price: String,
    val reviews: List<Review>
)