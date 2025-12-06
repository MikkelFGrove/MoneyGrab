package com.example.moneygrab

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.authentication.CurrentUser
import com.example.debtcalculator.data.User
import com.example.moneygrab.components.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import kotlin.coroutines.coroutineContext

class WebsocketClient(context: Context) {
    var instance: okhttp3.WebSocket? = null
    private var ids: List<APIEndpoints.GroupId>? = null

    fun connect(user: User) {
        if (instance != null) disconnect()

        runBlocking {
            launch {
                ids = RetrofitClient.getAPI().getGroupIds(user.id).body()
                println(ids)
                val request = Request.Builder().url("ws://10.0.2.2:3000").build()
                val client = OkHttpClient()
                client.newWebSocket(
                    request = request,
                    listener = listener,
                )
            }
        }
    }

    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private val listener = object : WebSocketListener() {
        override fun onMessage(
            webSocket: okhttp3.WebSocket,
            text: String
        ) {
            super.onMessage(webSocket, text)
            serviceScope.launch {
                println("Message received: $text")
                val activity = context as? MainActivity
                activity?.notifyUsers(text)
            }
        }

        override fun onClosed(
            webSocket: okhttp3.WebSocket,
            code: Int, reason: String
        ) {
            super.onClosed(webSocket, code, reason)
            serviceScope.launch {
                // send closed event
                println("WebSocket: Connection closed")
            }
        }

        override fun onOpen(
            webSocket: okhttp3.WebSocket,
            response: Response
        ) {
            super.onOpen(webSocket, response)
            serviceScope.launch {
                // send open event
                ids?.let {
                    for (id in it) {
                        webSocket.send("${id.id}")
                    }
                }
            }
        }

        override fun onFailure(
            webSocket: okhttp3.WebSocket,
            t: Throwable,
            response: Response?
        ) {
            super.onFailure(webSocket, t, response)
            serviceScope.launch {
                // send error event
                println(t.message)
            }
        }
    }

    fun disconnect() {
        instance?.close(1000, "Disconnected by client")
        instance = null
    }
}