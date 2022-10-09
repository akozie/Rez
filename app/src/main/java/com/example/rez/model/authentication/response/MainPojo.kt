package com.example.rez.model.authentication.response

import retrofit2.http.Query

data class MainPojo(
    val status: String,
    val predictions: ArrayList<ListClass>
)
