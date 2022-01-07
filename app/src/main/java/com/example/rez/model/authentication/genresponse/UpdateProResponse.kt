package com.example.rez.model.authentication.genresponse

import com.example.rez.model.authentication.request.UpdateProfileResponse

data class UpdateProResponse(
    val data: UpdateProfileResponse,
    val message: String,
    val status: Boolean
)