package com.example.rez.model.authentication.genresponse

import com.example.rez.model.authentication.response.ForgotPasswordResponse


data class ForPasswordResponse (
    val status : Boolean,
    val message : String,
    val data: ForgotPasswordResponse
)