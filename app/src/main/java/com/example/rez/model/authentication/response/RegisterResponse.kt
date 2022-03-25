package com.example.rez.model.authentication.response

data class RegisterResponse(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val active: Any,
    val avatar: String,
    val phone: String,
    val token: String
)