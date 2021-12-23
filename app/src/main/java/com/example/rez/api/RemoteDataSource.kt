package com.example.rez.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RemoteDataSource {
    companion object {
        private const val BASE_URL = "https://rez-staging.herokuapp.com/api/v1/"
    }
    fun <Api> buildApi(
        api:Class<Api>
    ): Api {
        val httpLoggingInterceptor  = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    //builder.header("MSHASH", "12345678900987654321")
                    return@Interceptor chain.proceed(builder.build())
                }
            ).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(api)
    }

}