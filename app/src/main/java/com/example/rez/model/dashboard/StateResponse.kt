package com.example.rez.model.dashboard

data class StateResponse(
    val `data`: List<States>,
    val message: String,
    val status: Boolean
)