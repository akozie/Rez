package com.example.rez.api

import com.example.rez.model.authentication.genresponse.LogResponse
import com.example.rez.model.authentication.request.ChangePasswordRequest
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.response.ChangePasswordResponse
import com.example.rez.model.authentication.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("user/auth/register")
    suspend fun register(
        @Body user: RegisterRequest
    ): RegResponse

    @POST("user/auth/login")
    suspend fun login(
        @Body user: LoginRequest
    ): LogResponse

    @PUT("user/auth/password/change")
    suspend fun changePassword(
        @Body user: ChangePasswordRequest
    ): ChangePasswordResponse


}