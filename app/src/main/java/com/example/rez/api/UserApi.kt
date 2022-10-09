package com.example.rez.api

import com.example.rez.model.authentication.request.UpdateProfileRequest
import com.example.rez.model.authentication.request.UpdateProfileResponse
import com.example.rez.model.authentication.response.LoginResponse
import com.example.rez.model.authentication.response.MainPojo
import com.example.rez.model.direction.DirectionResponseModel
import retrofit2.http.*

interface UserApi {

    @GET("maps/api/directions/json?")
    suspend fun getDirect(
        @Query("mode") mode: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): DirectionResponseModel

    @GET("maps/api/place/queryautocomplete/json")
    suspend fun getPlace(@Query("input") input: String,
                         @Query("key") key: String): MainPojo

}