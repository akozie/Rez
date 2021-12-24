package com.example.rez.model.authentication.response

data class RegisterResponse(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val password_confirmation: String,
    val phone: String
)