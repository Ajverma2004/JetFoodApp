package com.ajverma.jetfoodapp.data.network.auth

import android.content.Context
import android.content.SharedPreferences

class JetFoodSession(
    private val context: Context
) {
    private val sharedPres: SharedPreferences = context.getSharedPreferences(
        "jetfood",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        sharedPres.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        sharedPres.getString("token", null)?.let {
            return it
        }
        return null
    }


}