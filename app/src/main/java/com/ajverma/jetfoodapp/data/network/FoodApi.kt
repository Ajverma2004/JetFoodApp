package com.ajverma.jetfoodapp.data.network

import com.ajverma.jetfoodapp.data.network.models.authModels.AuthResponse
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {

    @GET("food")
    suspend fun getFood(): List<String>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("auth/login")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): AuthResponse
}