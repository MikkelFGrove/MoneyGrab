package com.example.moneygrab

import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class LoginRequest(
    val phone: String,
    val password: String
)
interface APIEndpoints {
    @POST("user/{id}/group/{groupId}")
    suspend fun payTransaction(
        @Path("id") userId: String,
        @Path("groupId") groupId: Int
    ): Response<Int>

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

    //TODO - This should either return a group with all expenses and their owners and payer or a separate endpoint should be made
    // Currently, the chatview crashes as it tries to create bubbles for null expenses
    @GET("groups/{id}")
    suspend fun getGroup(@Path("id") id: Int): Response<Group>

    @POST("groups/{id}/closetab")
    suspend fun closeTab(@Path("id") id: Int): Response<Int>

    @GET("/groups/{groupId}/{userId}/sum")
    suspend fun getAmountOwed(
        @Path("groupId") groupId: Int,
        @Path("userId") userId: Int
    ): Response<OwedAmount>

    @GET("/expenses/{expenseId}")
    suspend fun getExpense(@Path("expenseId") expenseId: Int): Response<Expense>

    @PUT("/expenses/{expenseId}")
    suspend fun updateExpense(@Path("expenseId") expenseId: Int, @Body body: Expense): Response<Int>

    @POST("/expenses")
    suspend fun createExpense(@Body body: Expense): Response<Int>

    @POST("users")
    suspend fun updateUser(@Body body: UpdateUser): Response<com.example.debtcalculator.data.User>

    @GET("/groups/{groupId}/expenses")
    suspend fun getExpensesInGroup(@Path("groupId") groupId: Int): Response<MutableList<ChatExpense>>

    @GET("/groups/{groupId}/users")
    suspend fun getUsersInGroup(@Path("groupId") groupId: Int): Response<MutableSet<User>>

    data class LoginData(
        val phoneNumber: String,
        val password: String
    )

    data class GroupData(
        val name: String,
        val description: String,
        val users: List<User>
    )

    data class SignupData(
        val phoneNumber: String,
        val password: String,
        val name: String,
        val image: String?
    )

    data class UpdateUser (
        val userId: Int,
        val phoneNumber: String,
        val name: String,
        val image: String?
    )

    data class ChatExpense(
        var id: Int,
        var amount: Float,
        var description: String,
        var group: Int,
        var owner: Int,
    )

    data class OwedAmount(
        var amount: Float
    )
}