package com.example.rez.model.dashboard

data class HomeResponse(
    val `data`: List<DataXX>,
    val message: String,
    val status: Boolean
)