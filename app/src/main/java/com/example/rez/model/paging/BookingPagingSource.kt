package com.example.rez.model.paging



import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.Booking
import retrofit2.HttpException
import java.io.IOException

class BookingPagingSource(
    private val authApi: AuthApi,
    val token: String
) : PagingSource<Int, Booking>() {
    override fun getRefreshKey(state: PagingState<Int, Booking>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Booking> {
        val currentPage = params.key ?: 1

        return try {
            val response = authApi.getBookingsHistory(token, currentPage)
            val responseData = mutableListOf<Booking>()
            val bookings = response.body()?.data?.bookings?.toList()
           // responseData.addAll(bookings)
//            delay(5000)

            LoadResult.Page(
                data = bookings!!,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (bookings.isEmpty()) null else currentPage + 1
//                nextKey = if (response.body()?.links?.to!! >= response.body()?.links?.from!!) null else currentPage + 1
            )
        } catch (exception : IOException) {
                LoadResult.Error(exception)
        } catch (exception: HttpException)  {
                LoadResult.Error(exception)
        }
    }

}