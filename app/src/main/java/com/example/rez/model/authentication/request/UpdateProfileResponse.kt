package com.example.rez.model.authentication.request

data class UpdateProfileResponse(
    val active: Int,
    val avatar: Any,
    val email: String?,
    val email_verified: Boolean,
    val first_name: String?,
    val id: String?,
    val last_name: String?,
    val phone: String?
)