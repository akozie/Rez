package com.example.rez.model.authentication.response

data class LoginResponse(
    val data: Data,
    val message: String,
    val status: Boolean
)