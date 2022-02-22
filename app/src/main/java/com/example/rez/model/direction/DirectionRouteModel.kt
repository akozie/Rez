package com.example.rez.model.direction

data class DirectionRouteModel(
    val legs: List<DirectionLegModel>,
    val overview_polyline: DirectionPolylineModel,
    val summary: String
)
