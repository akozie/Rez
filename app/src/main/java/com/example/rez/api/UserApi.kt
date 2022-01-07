package com.example.rez.api

import com.example.rez.model.authentication.request.UpdateProfileRequest
import com.example.rez.model.authentication.request.UpdateProfileResponse
import com.example.rez.model.authentication.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserApi {

    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): LoginResponse

    @PUT("user")
    suspend fun updateProfile(
        @Body user: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): UpdateProfileResponse
}