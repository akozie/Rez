package com.example.rez.model.paging



import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rez.api.AuthApi
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.dashboard.Notification
import retrofit2.HttpException
import java.io.IOException

class NotificationPagingSource(
    private val authApi: AuthApi,
    val token: String
) : PagingSource<Int, Notification>() {
    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val currentPage = params.key ?: 1

        return try {
            val response = authApi.getNotification(token, currentPage)
            val bookings = response.body()?.data?.notifications!!.toList()


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