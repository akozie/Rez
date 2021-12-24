package com.example.rez.model.authentication.response

data class ResetPasswordOTPResponse(
    val code: String,
    val reference: String
)