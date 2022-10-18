package com.example.rez.repository


import android.util.Log
import com.example.rez.api.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException


//abstract class BaseRepository {
//
//
//    suspend fun <T> safeApiCall(
//        apiCall: suspend () -> T
//    ): Resource<T> {
//        return withContext(Dispatchers.IO){
//            try {
//                Resource.Success(apiCall.invoke())
//                //Resource.Failure(false,null,null, apiCall.invoke())
//            } catch (throwable: Throwable){
//                when(throwable){
//                    is HttpException -> {
//                        Log.d("FAILURECHECKLOOP", throwable.message())
//                        val error =  throwable.response()?.errorBody()?.toString()
//                        val message  = StringBuilder()
//                        error?.let {
//                            try {
//                                val YourErrorResponseClassObj = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), YourErrorResponseClass::class.java)
//                                Log.d("NEWERRRRORRR", YourErrorResponseClassObj.toString())
//                                Resource.Failure(false, throwable.code(), YourErrorResponseClassObj)
//                            }catch (e:JSONException){ }
//                            message.append("\n")
//                        }
//                        Resource.Error(false, throwable.code(), throwable.response()?.errorBody())
//                    }else -> {
//                    //Log.d("FAILURECHECK", throwable.localizedMessage)
//                    Resource.Failure(true, null)
//                    //Resource.Error(true, null, null)
//                }
//                }
//            }
//        }
//    }
//}

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
                                val YourErrorResponseClassObj = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), YourErrorResponseClass::class.java)
                                Log.d("NEWERRRRORRR", YourErrorResponseClassObj.toString())
                        if (YourErrorResponseClassObj.errors == null){
                            Resource.Error(false, throwable.code(), YourErrorResponseClassObj.message)
                        }else{
                            Resource.Error(false, throwable.code(), YourErrorResponseClassObj.errors)
                        }
                        //Resource.Failure(false, throwable.code())
                    }else -> {
                    //Log.d("FAILURE", throwable.message.toString())
                    Resource.Error(true, null, "Please check your internet connection and try again")
                    //Resource.Failure(true, null)
                }
                }
            }
        }
    }
}

//abstract class BaseRepository {
//
//
//    suspend fun <T: Any> safeApiCall(call: suspend () -> Response<T>): T {
//        val response = call.invoke()
//        if (response.isSuccessful){
//            return response.body()!!
//        }else {
//            val error =  response.errorBody()?.toString()
//            val message  = StringBuilder()
//            error?.let {
//                try {
//                    message.append(JSONObject(it).getString("errors"))
//                }catch (e:JSONException){ }
//                message.append("\n")
//            }
//            message.append("Error code: ${response.code()}")
//            throw ApiException(message.toString())
//        }
//    }
//}
//class ApiException(message:String) : IOException(message)

data class YourErrorResponseClass(
    val status: Boolean,
    val message: String,
    val errors : String?
)