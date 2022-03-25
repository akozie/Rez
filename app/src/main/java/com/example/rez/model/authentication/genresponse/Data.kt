package com.example.rez.model.authentication.genresponse

data class Data(
    val active: Int,
    val avatar: String,
    val email: String,
    val email_verified: Int,
    val first_name: String,
    val id: Int,
    val last_name: String,
    val phone: Any,
    val token: String
)