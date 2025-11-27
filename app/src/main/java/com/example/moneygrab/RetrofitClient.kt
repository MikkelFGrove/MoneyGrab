package com.example.moneygrab

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .build()

        val instance = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val api: APIEndpoints = instance.create<APIEndpoints>(APIEndpoints::class.java)

        fun getAPI(): APIEndpoints {
            return api;
        }
    }
}