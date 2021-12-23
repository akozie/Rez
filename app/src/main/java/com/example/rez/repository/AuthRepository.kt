package com.example.rez.repository


import com.example.rez.api.AuthApi
import com.example.rez.model.authentication.request.LoginRequest

class AuthRepository(
    private val api: AuthApi
): BaseRepository() {

    suspend fun login( user: LoginRequest) = safeApiCall{
        api.login(user)
    }
}