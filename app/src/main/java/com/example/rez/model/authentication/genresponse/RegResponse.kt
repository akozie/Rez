package com.example.rez.model.authentication.genresponse

data class RegResponse (
    val status : Boolean,
    val message : String,
    val data: RegistrationSuccessData?,
    val errors:RegistrationErrorData?
)

data class RegistrationSuccessData(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val active: Any,
    val avatar: String,
    val phone: String,
    val token: String
)
data class RegistrationErrorData(
    val email: List<String>,
    val phone: List<String>,
)


//{"status":false,"message":"Error in data sent","errors":{"email":["The email has already been taken."],"phone":["The phone field contains an invalid number."]}}