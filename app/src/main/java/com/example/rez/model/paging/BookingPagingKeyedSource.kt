package com.example.rez.model.paging

import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.BookingsHistoryResponse
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

//class BookingPagingKeyedSource(
//    private val authApi: AuthApi,
//    val token: String
//) : PageKeyedDataSource<Int, Booking>() {
//
//        override fun loadInitial(params: LoadInitialParams<Int>,
//                                         callback: LoadInitialCallback<Int, Booking>) {
//
//            authApi.getBookings(
//                page = 1,
//                per_page = 20,
//                token = token).enqueue(object : Callback<BookingsHistoryResponse>{
//                override fun onResponse(
//                    call: Call<BookingsHistoryResponse>,
//                    response: Response<BookingsHistoryResponse>
//                ) {
//                    if (response.body() != null){
//                            callback.onResult(response.body()?.data?.bookings, null, 1+1)
//                    }
//                }
//
//                override fun onFailure(call: Call<BookingsHistoryResponse>, t: Throwable) {
//
//                }
//
//            })
//        }
//
//        override fun loadAfter(params: LoadParams<Int>,
//                                       callback: LoadCallback<Int, Booking>) {
//
//
//            authApi.getBookings(
//                page = 1,
//                per_page = 20,
//                token = token).enqueue(object : Callback<BookingsHistoryResponse>{
//                override fun onResponse(
//                    call: Call<BookingsHistoryResponse>,
//                    response: Response<BookingsHistoryResponse>
//                ) {
//                    if (response.body() != null){
//                        val key = response.body()!!.links.next_page_url ?: params.key + 1 : null
//                    }
//                        callback.onResult(response.body()?.data?.bookings, key)
//                    }
//        }
//
//    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Booking>) {
//
//    }
//}
//}
