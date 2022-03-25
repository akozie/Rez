package com.example.rez.model.authentication.response

data class LinksXX(
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: String,
    val to: Int
)