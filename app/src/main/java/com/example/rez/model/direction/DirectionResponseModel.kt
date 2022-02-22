package com.example.rez.model.direction


data class DirectionResponseModel(
    val routes: List<DirectionRouteModel>,
    val error_message: String
)
