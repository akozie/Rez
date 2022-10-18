package com.example.rez.model.authentication.request

data class RegisterRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val fcm_token: String,
    val password: String,
    val password_confirmation: String,
    val phone: String
)