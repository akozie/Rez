package com.example.rez.model.authentication.response

data class ChangePasswordResponse(
    val errors: Errors,
    val message: String,
    val status: Boolean
)