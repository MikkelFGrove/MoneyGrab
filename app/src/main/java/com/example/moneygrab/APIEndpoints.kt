package com.example.moneygrab

import com.example.moneygrab.views.User
import retrofit2.http.GET
import retrofit2.http.Path

interface APIEndpoints {
    @GET("users/search/{search}")
    suspend fun getSuggestedUsers(@Path("search") search: String): List<User>
}