package com.example.rez.model.dashboard

data class ReviewResponse(
    val `data`: DataXXX,
    val links: Links,
    val message: String,
    val status: Boolean
)