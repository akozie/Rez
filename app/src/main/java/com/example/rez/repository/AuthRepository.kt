package com.example.rez.repository


import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.rez.api.AuthApi
import com.example.rez.api.RemoteDataSource.Companion.api
import com.example.rez.api.RemoteDirectionDataSource.Companion.googleapi
import com.example.rez.model.authentication.request.*
import com.example.rez.model.dashboard.BookTableRequest
import com.example.rez.model.paging.BookingPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository: BaseRepository() {
    //var favorite = 0

    suspend fun getDirection(mode: String, origin: String, destination: String, key: String) = safeApiCall {
        googleapi.getDirect(mode, origin, destination, key)
    }

    suspend fun addVendorRating(token: String, rating: Int, vendorInt: Int) = safeApiCall{
        api.addVendorRating(token, rating, vendorInt)
    }

    suspend fun addTableReview(token: String, tableReviewRequest: TableReviewRequest, vendorInt: Int, tableId: Int) = safeApiCall{
        api.addTableReview(token, tableReviewRequest, vendorInt, tableId)
    }

    suspend fun bookTable(token: String, bookTableRequest: BookTableRequest) = safeApiCall{
        api.bookTable(token, bookTableRequest)
    }


//    fun getBookings(token: String) = Pager(PagingConfig(pageSize = 100, enablePlaceholders = false)){
//        BookingPagingSource(api, token)
//    }.liveData


//    suspend fun getBookingsHistory(token: String, page: Int) = safeApiCall{
//        api.getBookingsHistory(token, page)
//    }

//    suspend fun getBookingsHistory(token: String) = safeApiCall{
//        Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                maxSize = 100,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = {
//                BookingPagingSource(api, token)
//            }
//        ).liveData
//       // api.getBookingsHistory(token)
//    }

    suspend fun getVendorProfileTableReviews(vendorInt: Int, tableId: Int, token: String) = safeApiCall{
        api.getVendorProfileTableReviews(vendorInt, tableId, token)
    }

    suspend fun getVendorProfileTable(vendorInt: Int, tableId: Int, token: String) = safeApiCall{
        api.getVendorProfileTable(vendorInt, tableId, token)
    }

    suspend fun getVendorTables(vendorInt: Int, token: String) = safeApiCall{
        api.getVendorTables(vendorInt, token)
    }

    suspend fun search(search: String, token: String) = safeApiCall{
        api.search(search, token)
    }

    suspend fun getHome(lat: Double, long: Double, token: String) = safeApiCall{
        api.getHome(lat, long, token)
    }

    suspend fun getFavorites(token: String) = safeApiCall{
        api.getFavorites(token)
    }

    suspend fun addOrRemoveFavorites(id: String, token: String) = safeApiCall{
        api.addOrRemoveFavorites(id, token)
    }

    suspend fun uploadImage(image: String, token: String) = safeApiCall{
        api.uploadImage(image,token)
    }

    suspend fun getProfile( token: String) = safeApiCall{
        api.getProfile(token)
    }

    suspend fun updateProfile(user:UpdateProfileRequest, token: String) = safeApiCall{
        api.updateProfile(user, token)
    }

    suspend fun resetPassword( user: ResPasswordRequest) = safeApiCall{
        api.resetPassword(user)
    }

    suspend fun forgotPassword( user: ForgotPasswordRequest) = safeApiCall{
        api.forgotPassword(user)
    }

    suspend fun register( user: RegisterRequest) = safeApiCall{
        api.register(user)
    }

    suspend fun login( user: LoginRequest) = safeApiCall{
        api.login(user)
    }
    suspend fun loginWithGoogle(token: String) = safeApiCall{
        api.loginWithGoogle(token)
    }

    suspend fun changePassword(user: ChangePasswordRequest, token: String) = safeApiCall{
        api.changePassword(user, token)
    }
}