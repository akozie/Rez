package com.example.rez.api

import android.net.Uri
import com.example.rez.model.authentication.genresponse.*
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
        @Body rating: RateVendorRequest,
        @Path("vendorProfileID") vendorProfileID: Int,
    ): GeneralResponse

    @DELETE("user/reviews/{reviewID}")
    suspend fun deleteTableReview(
        @Header("Authorization") token: String,
        @Path("reviewID") reviewID: Int
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
        ): Response<BookingsHistoryResponse>

    @GET("user/bookings/{bookingID}")
    suspend fun getEachBooking(
        @Header("Authorization") token: String,
        @Path("bookingID") bookingID: Int,
        ): GetEachBookingResponse

    @GET("user/vendorProfile/{vendorProfileID}/table/{tableID}/reviews")
    suspend fun getVendorProfileTableReviews(
        @Path("vendorProfileID") vendorProfileID: Int,
        @Path("tableID") tableID: Int,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        ): Response<ReviewResponse>

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
        @Query("no_persons") no_persons: Int,
        @Query("price_from") price_from: Int,
        @Query("price_to") price_to: Int,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        ): Response<SearchResponse>

    @GET("user/home")
    suspend fun getHome(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Header("Authorization") token: String
    ): HomeResponse

    @GET("user/favourites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        ): Response<GetFavoritesResponse>

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

    @POST("user/auth/login/google/callback/android")
    suspend fun loginWithGoogle(
        @Body accessToken: FacebookRequest
    ): SocialResponse

    @POST("user/auth/login/facebook/callback/android")
    suspend fun loginWithFacebook(
        @Body access_token: FacebookRequest
    ): SocialResponse

    @PUT("auth/password/change")
    suspend fun changePassword(
        @Body user: ChangePasswordRequest,
        @Header("Authorization") token: String
    ): ChangePasswordResponse


}