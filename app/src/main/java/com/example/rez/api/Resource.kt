package com.example.rez.api

import com.example.rez.model.authentication.genresponse.RegResponse
import okhttp3.ResponseBody

sealed class  Resource<out T> {
    data class Success<out T>(val value: T): Resource<T>()
    data class Error<out T>(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val data: T,
    ): Resource<Nothing>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        ): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}
//sealed class  Resources<out T> {
//    data class Success<out T>(val value: T): Resource<T>()
//    data class Failure<T>(
//        val isNetworkError: Boolean,
//        val errorCode: Int?,
//        val data: T?,
//    ): Resource(data)
//    object Loading: Resource<Nothing>()
//}