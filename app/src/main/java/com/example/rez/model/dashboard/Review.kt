package com.example.rez.model.dashboard

data class Review(
    val created_at: String,
    val id: Int,
    val rating: Int,
    val review: String,
    val table_id: Int,
    val updated_at: String,
    val user_profile_id: Int
)