package com.example.moneygrab

import com.example.moneygrab.views.User
import retrofit2.Response
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
    suspend fun getSuggestedUsers(@Path("search") search: String): Response<List<User>>

    @POST("groups")
    suspend fun createGroup(@Body body: GroupData): Response<Int>

    @POST("login")
    suspend fun login(@Body body: LoginData): Response<com.example.debtcalculator.data.User>

    data class LoginData (
        val phoneNumber: String,
        val password: String
    )

    data class GroupData (
        val name: String,
        val users: List<User>
    )
}