package com.example.rez.model.authentication.response

data class Errors(
    val current_password: List<String>,
    val password: List<String>
)