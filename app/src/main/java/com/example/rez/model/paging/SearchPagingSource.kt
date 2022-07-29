package com.example.rez.model.paging

import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.model.dashboard.ReviewX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    val value: String,
    private val noOfPersons: String?,
    private val priceFrom: Int?,
    private val  priceTo: Int?,
    private val  stateID: Int?,
    val  type: Int?,
    private val authApi: AuthApi,
    val token: String
) : PagingSource<Int, ResultX>() {
    override fun getRefreshKey(state: PagingState<Int, ResultX>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultX> {
        val currentPage = params.key ?: 1

        return try {
            val response = authApi.search(value, noOfPersons, priceFrom, priceTo, stateID, type, token, currentPage)
            val bookings = response.body()?.data?.results!!.toList()


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