package com.example.rez.model.authentication.response

data class Data(
    val active: Int,
    val avatar: Any,
    val email_verified: Int,
    var first_name: String,
    val id: Int,
    val last_name: String,
    val phone: String,
    val token: String?
)