package com.example.rez.model.dashboard

data class ReviewX(
    val created_at: String,
    val id: Int,
    val review: String,
    val user: User
)