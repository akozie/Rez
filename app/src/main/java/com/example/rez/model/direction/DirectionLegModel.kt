package com.example.rez.model.direction



data class DirectionLegModel(
    val distance: DirectionDistanceModel,
    val duration: DirectionDurationModel,
    val end_address: String,
    val end_location: EndLocationModel,
    val start_address: String,
    val start_location: StartLocationModel,
    val steps: List<DirectionStepModel>
)
