package com.example.moneygrab

import com.example.moneygrab.views.GroupData
import com.example.moneygrab.views.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val phone: String,
    val password: String
)
interface APIEndpoints {
    @GET("users/search/{search}")
    suspend fun getSuggestedUsers(@Path("search") search: String): List<User>

    @POST("groups")
    suspend fun createGroup(@Body body: GroupData)

    @POST("login")
    suspend fun login(
        @Body credentials: Map<String, String>): com.example.debtcalculator.data.User

    @POST("signup")
    suspend fun signup(
        @Body info: Map<String, String>): com.example.debtcalculator.data.User
}