package com.example.rez.model.authentication.request

import retrofit2.http.Query

data class SearchQuery(
    @Query("search") val search: String
)