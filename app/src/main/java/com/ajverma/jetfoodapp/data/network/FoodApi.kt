package com.ajverma.jetfoodapp.data.network

import retrofit2.http.GET

interface FoodApi {

    @GET("food")
    suspend fun getFood(): List<String>
}