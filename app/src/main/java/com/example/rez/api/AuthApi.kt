package com.example.rez.api

import com.example.rez.model.authentication.genresponse.ForPasswordResponse
import com.example.rez.model.authentication.genresponse.LogResponse
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.genresponse.ResPasswordResponse
import com.example.rez.model.authentication.request.*
import com.example.rez.model.authentication.response.ChangePasswordResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @PUT("auth/password/reset")
    suspend fun resetPassword(
        @Body user: ResPasswordRequest
    ): ResPasswordResponse

    @POST("auth/password/forgot")
    suspend fun forgotPassword(
        @Body user: ForgotPasswordRequest
    ): ForPasswordResponse

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