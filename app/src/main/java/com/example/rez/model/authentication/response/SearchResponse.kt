package com.example.rez.model.authentication.response

data class SearchResponse(
    val `data`: DataXX,
    val links: LinksX,
    val message: String,
    val status: Boolean
)