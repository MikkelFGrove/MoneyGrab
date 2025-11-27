package com.example.moneygrab

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import kotlin.math.min

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelay: Long = 1000L,
    private val maxDelay: Long = 30000L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var delay = initialDelay

        while (true) {
            try {
                val response = chain.proceed(chain.request())

                return response
            } catch (e: Exception) {
                if (attempt > maxRetries) {
                    return Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .message("Network error")
                        .code(523)
                        .body(
                            ResponseBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                "Failed to establish network"
                            ))
                        .build()
                }
            }
            attempt++
            Thread.sleep(delay)
            delay = min(delay * 2, maxDelay)
        }
    }
}

