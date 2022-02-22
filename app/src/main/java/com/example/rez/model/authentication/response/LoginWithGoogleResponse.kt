package com.example.rez.model.authentication.response

data class LoginWithGoogleResponse(
    val message: String,
    val status: Boolean,
    val data: String
)