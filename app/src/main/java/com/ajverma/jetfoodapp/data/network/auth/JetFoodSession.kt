package com.ajverma.jetfoodapp.data.network.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class JetFoodSession(
    private val context: Context
) {
    private val sharedPres: SharedPreferences = context.getSharedPreferences(
        "jetfood",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        Log.d("JetFoodSession", "Saving token: $token")
        if (token.isBlank()) {
            Log.e("JetFoodSession", "Attempted to save blank token")
            return
        }
        sharedPres.edit()
            .putString("token", token)
            .apply()
    }

    fun getToken(): String? {
        val token = sharedPres.getString("token", null)
        if (token != null) {
            // Basic JWT format validation
            if (!token.contains(".") || token.count { it == '.' } != 2) {
                Log.e("JetFoodSession", "Invalid token format stored")
                return null
            }
            Log.d("JetFoodSession", "Retrieved valid token: ${token.take(20)}...")
        } else {
            Log.w("JetFoodSession", "No token found in SharedPreferences")
        }
        return token
    }

    fun clearToken() {
        Log.d("JetFoodSession", "Clearing stored token")
        sharedPres.edit().remove("token").apply()
    }
}