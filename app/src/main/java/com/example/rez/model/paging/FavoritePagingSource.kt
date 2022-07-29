package com.example.rez.model.paging

import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Favourite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FavoritePagingSource(
    private val authApi: AuthApi,
    val token: String,
    private val stateId: Int
) : PagingSource<Int, Favourite>() {
    override fun getRefreshKey(state: PagingState<Int, Favourite>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Favourite> {
        val currentPage = params.key ?: 1

        return try {
            val response = authApi.getFavoritesState(token, stateId, currentPage)
            val bookings = response.body()?.data?.favourites!!.toList()


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