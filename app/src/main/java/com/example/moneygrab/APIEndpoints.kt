package com.example.moneygrab

import com.example.debtcalculator.data.Group
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

    @GET("/users/{id}/groups")
    suspend fun getGroups(@Path("id") id: Int): Response<List<Group>>

    @POST("login")
    suspend fun login(@Body body: LoginData): Response<com.example.debtcalculator.data.User>

    @POST("users")
    suspend fun signup(@Body body: SignupData): Response<com.example.debtcalculator.data.User>

    @GET("groups/{id}")
    suspend fun getGroup(@Path("id") id: Int): Response<Group>

    @POST("groups/{id}/closetab")
    suspend fun closeTab(@Path("id") id: Int): Response<Int>

    @GET("groups/{groupId}/owed/{userPhone}")
    suspend fun getAmountOwed(@Path("groupId") groupId: Int, @Path("userId") userPhone: String): Response<Float>

    data class LoginData (
        val phoneNumber: String,
        val password: String
    )

    data class GroupData (
        val name: String,
        val users: List<User>
    )

    data class SignupData (
        val phoneNumber: String,
        val password: String,
        val name: String,
        val image: String?
    )
}