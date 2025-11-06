package com.example.moneygrab

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance{
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: APIEndpoints by lazy {
        retrofit.create(APIEndpoints::class.java)
    }
}