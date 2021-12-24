package com.example.rez.model.authentication.response

data class ChangePasswordResponse(
    val current_password: String,
    val password: String,
    val password_confirmation: String
)