package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItemResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface RestaurantRepository {
    suspend fun getRestaurantMenu(restaurantId: String): Resource<FoodItemResponse>
}