package com.example.rez.model.authentication.response

data class GetFavoritesResponse(
    val `data`: DataX,
    val links: Links,
    val message: String,
    val status: Boolean
)