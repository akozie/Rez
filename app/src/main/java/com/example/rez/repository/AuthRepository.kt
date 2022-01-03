package com.example.rez.repository


import com.example.rez.api.RemoteDataSource.Companion.api
import com.example.rez.model.authentication.request.ForgotPasswordRequest
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.model.authentication.request.ResPasswordRequest

class AuthRepository: BaseRepository() {

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
}