package com.example.rez.model.direction


data class DirectionStepModel (
    var distance: DirectionDistanceModel,
    var duration: DirectionDurationModel,
    var end_location: EndLocationModel,
    var html_instructions: String,
    var polyline: DirectionPolylineModel,
    var start_location: StartLocationModel,
    var travel_mode: String,
    var maneuver: String

)