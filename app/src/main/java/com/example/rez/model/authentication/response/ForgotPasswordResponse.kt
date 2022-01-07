package com.example.rez.model.authentication.response

data class ForgotPasswordResponse(
    val verified: Boolean,
    val reference: String
)