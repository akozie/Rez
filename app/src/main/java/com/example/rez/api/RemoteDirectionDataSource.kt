package com.example.rez.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class RemoteDirectionDataSource {
        companion object {
            private const val BASE_URL = "https://maps.googleapis.com/"

//            private val retrofit: Retrofit by lazy {
//                Retrofit.Builder()
//                    .addConverterFactory(MoshiConverterFactory.create())
//                    .baseUrl(BASE_URL)
//                    .build()
//            }

            private val retrofit by lazy {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }

            val googleapi: UserApi by lazy {
                retrofit.create(UserApi::class.java)
            }
        }
    }

