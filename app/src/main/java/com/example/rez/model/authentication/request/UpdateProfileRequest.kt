package com.example.rez.model.authentication.request

data class UpdateProfileRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val phone: String
)