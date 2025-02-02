package com.ajverma.jetfoodapp.data.network

import com.ajverma.jetfoodapp.data.network.models.authModels.AuthResponse
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.CategoriesResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItemResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.RestaurantResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {

    @GET("categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): RestaurantResponse

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): FoodItemResponse

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("auth/login")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): AuthResponse
}