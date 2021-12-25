package com.example.rez.model.authentication.genresponse

import com.example.rez.model.authentication.response.RegisterResponse

data class LogResponse (
    val status : Boolean,
    val message : String,
    val data: LogResponse
)