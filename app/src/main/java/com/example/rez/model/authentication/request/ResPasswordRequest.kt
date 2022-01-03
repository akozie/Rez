package com.example.rez.model.authentication.request

import com.example.rez.model.authentication.response.ForgotPasswordResponse


data class ResPasswordRequest (
    val password: String,
    val password_confirmation: String,
    val reference: String
    )