package com.example.rez.repository


import android.util.Log
import com.example.rez.api.Resource
import com.example.rez.model.authentication.genresponse.RegResponse
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
                        //Log.d("FAILURECHECKLOOP", throwable.localizedMessage)
                        Resource.Failure(false, throwable.code(), throwable.response())
                    }else -> {
                    //Log.d("FAILURECHECK", throwable.localizedMessage)
                    Resource.Failure(true, null,  null)
                }
                }
            }
        }
    }
}