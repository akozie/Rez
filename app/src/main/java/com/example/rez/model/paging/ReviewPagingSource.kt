package com.example.rez.model.paging

import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.dashboard.ReviewX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ReviewPagingSource(
    val vendorID: Int,
    val tableID: Int,
    private val authApi: AuthApi,
    val token: String
) : PagingSource<Int, ReviewX>() {
    override fun getRefreshKey(state: PagingState<Int, ReviewX>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewX> {
        val currentPage = params.key ?: 1

        return try {
            val response = authApi.getVendorProfileTableReviews(vendorID, tableID, token, currentPage)
            val bookings = response.body()?.data?.reviews!!.toList()


            LoadResult.Page(
                data = bookings,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (bookings.isEmpty()) null else currentPage + 1
            )
        } catch (exception : IOException) {
                LoadResult.Error(exception)
        } catch (exception: HttpException)  {
                LoadResult.Error(exception)
        }
    }

}