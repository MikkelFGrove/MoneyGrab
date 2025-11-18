package com.example.moneygrab

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private val instance = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val api: APIEndpoints = instance.create<APIEndpoints>(APIEndpoints::class.java)

        fun getAPI(): APIEndpoints {
            return api;
        }
    }
}