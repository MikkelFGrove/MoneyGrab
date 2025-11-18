package com.example.authentication

import android.content.Context
import com.example.debtcalculator.data.User

class CurrentUser(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit().apply {
            putInt("id", user.id)
            putString("phone", user.phoneNumber)
            putString("username", user.name)
            apply()
        }
    }

    fun getUser(): User? {
        val id = prefs.getInt("id", -1)
        val phone = prefs.getString("phone", null) ?: return null
        val name = prefs.getString("username", null) ?: return null
        return User(id, phone, name, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
