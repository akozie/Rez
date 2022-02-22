package com.example.rez.api

import android.net.Uri
import com.example.rez.model.authentication.genresponse.ForPasswordResponse
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.genresponse.ResPasswordResponse
import com.example.rez.model.authentication.genresponse.UpdateProResponse
import com.example.rez.model.authentication.request.*
import com.example.rez.model.authentication.response.*
import com.example.rez.model.dashboard.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
interface AuthApi {

    @PUT("user/vendorProfile/{vendorProfileID}/rate")
    suspend fun addVendorRating(
        @Header("Authorization") token: String,
        @Field("rating") rating: Int,
        @Path("vendorProfileID") vendorProfileID: Int,
    ): GeneralResponse

    @POST("user/vendorProfile/{vendorProfileID}/table/{tableID}/reviews")
    suspend fun addTableReview(
        @Header("Authorization") token: String,
        @Body tableReviewRequest: TableReviewRequest,
        @Path("vendorProfileID") vendorProfileID: Int,
        @Path("tableID") tableID: Int
    ): GeneralResponse

    @POST("user/book/table")
    suspend fun bookTable(
        @Header("Authorization") token: String,
        @Body bookTableRequest: BookTableRequest
    ): BookTableResponse

    @GET("user/bookings")
    suspend fun getBookingsHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
        ): Response<BookingsHistoryResponse>

    @GET("user/vendorProfile/{vendorProfileID}/table/{tableID}/reviews")
    suspend fun getVendorProfileTableReviews(
        @Path("vendorProfileID") vendorProfileID: Int,
        @Path("tableID") tableID: Int,
        @Header("Authorization") token: String
    ): ReviewResponse

    @GET("user/vendorProfile/{vendorProfileID}/table/{tableID}")
    suspend fun getVendorProfileTable(
        @Path("vendorProfileID") vendorProfileID: Int,
        @Path("tableID") tableID: Int,
        @Header("Authorization") token: String
    ): GetProfileTableResponse

    @GET("user/vendorProfile/{vendorProfileID}")
    suspend fun getVendorTables(
        @Path("vendorProfileID") vendorProfileID: Int,
        @Header("Authorization") token: String
    ): GetVendorTableResponse

    @GET("user/home/search")
    suspend fun search(
        @Query("q") q: String,
        @Header("Authorization") token: String
    ): SearchResponse

    @GET("user/home")
    suspend fun getHome(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Header("Authorization") token: String
    ): HomeResponse

    @GET("user/favourites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): GetFavoritesResponse

    @PUT("user/favourites/{vendorID}")
    suspend fun addOrRemoveFavorites(
        @Path("vendorID") vendorID:String,
        @Header("Authorization") token: String
    ): GeneralResponse

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

    @POST("user/auth/login/in/google/callback")
    suspend fun loginWithGoogle(
        @Header("Authorization") token: String
    ): LoginWithGoogleResponse

    @PUT("auth/password/change")
    suspend fun changePassword(
        @Body user: ChangePasswordRequest,
        @Header("Authorization") token: String
    ): ChangePasswordResponse


}