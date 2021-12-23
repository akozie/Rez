package com.example.rez.api

import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("user/auth/login")
    suspend fun login(
        @Body user: LoginRequest
    ): LoginResponse


}