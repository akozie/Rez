package com.example.rez.model.authentication.response

data class ResetPasswordResponse(
    val password: String,
    val password_confirmation: String,
    val reference: String
)