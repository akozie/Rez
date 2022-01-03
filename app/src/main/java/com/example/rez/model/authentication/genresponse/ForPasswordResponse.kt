package com.example.rez.model.authentication.genresponse

import com.example.rez.model.authentication.response.ForgotPasswordResponse
import com.example.rez.model.authentication.response.ResetPasswordResponse


data class ForPasswordResponse (
    val status : Boolean,
    val message : String,
    val data: ResetPasswordResponse
)