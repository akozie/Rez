package com.example.rez.repository


import android.net.Uri
import com.example.rez.api.RemoteDataSource.Companion.api
import com.example.rez.model.authentication.request.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository: BaseRepository() {

    suspend fun uploadImage(image: MultipartBody.Part, token: String) = safeApiCall{
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

    suspend fun changePassword(user: ChangePasswordRequest, token: String) = safeApiCall{
        api.changePassword(user, token)
    }
}