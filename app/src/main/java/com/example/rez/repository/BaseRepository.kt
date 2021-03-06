package com.example.rez.repository


import android.util.Log
import com.example.rez.api.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO){
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(false, throwable.code())
                    }else -> {
                   // Log.d("FAILURE", throwable.localizedMessage)
                    Resource.Failure(true, null)
                }
                }
            }
        }
    }
}