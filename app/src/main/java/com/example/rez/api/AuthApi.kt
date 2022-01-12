package com.example.rez.api

import android.net.Uri
import com.example.rez.model.authentication.genresponse.ForPasswordResponse
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.genresponse.ResPasswordResponse
import com.example.rez.model.authentication.genresponse.UpdateProResponse
import com.example.rez.model.authentication.request.*
import com.example.rez.model.authentication.response.ChangePasswordResponse
import com.example.rez.model.authentication.response.LoginResponse
import com.example.rez.model.authentication.response.UploadImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
interface AuthApi {

    @FormUrlEncoded
    @PUT("user/avatar")
    suspend fun uploadImage(
        @Field("avatar") avatar: String,
        @Header("Authorization") token: String
    ): UploadImageResponse


    @GET("user")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): LoginResponse

    @PUT("user")
    suspend fun updateProfile(
        @Body user: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): UpdateProResponse

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
    ): LoginResponse

    @PUT("auth/password/change")
    suspend fun changePassword(
        @Body user: ChangePasswordRequest,
        @Header("Authorization") token: String
    ): ChangePasswordResponse


}