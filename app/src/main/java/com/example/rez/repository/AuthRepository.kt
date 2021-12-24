package com.example.rez.repository


import com.example.rez.api.AuthApi
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.request.RegisterRequest

class AuthRepository(
    private val api: AuthApi
): BaseRepository() {

    suspend fun register( user: RegisterRequest) = safeApiCall{
        api.register(user)
    }

    suspend fun login( user: LoginRequest) = safeApiCall{
        api.login(user)
    }
}