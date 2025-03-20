package com.ajverma.jetfoodapp.domain.repositories.restaurant

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.domain.utils.Resource

interface RestaurantProfileRepository {
    suspend fun getRestaurantProfile(): Resource<Restaurant>
}