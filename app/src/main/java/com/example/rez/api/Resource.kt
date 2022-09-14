package com.example.rez.api

import com.example.rez.model.authentication.genresponse.RegResponse
import okhttp3.ResponseBody

sealed class  Resource<out T> {
    data class Success<out T>(val value: T): Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val message: Any?
        //val value: Any?
    ): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}