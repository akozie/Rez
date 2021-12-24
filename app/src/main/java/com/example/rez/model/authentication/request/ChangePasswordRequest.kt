package com.example.rez.model.authentication.request

data class ChangePasswordRequest(
    val current_password: String,
    val password: String,
    val password_confirmation: String
)