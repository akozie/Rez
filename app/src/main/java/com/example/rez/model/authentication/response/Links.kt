package com.example.rez.model.authentication.response

data class Links(
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int
)