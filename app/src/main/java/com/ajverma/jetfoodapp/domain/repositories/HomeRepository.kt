package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.CategoriesResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.RestaurantResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface HomeRepository {
    suspend fun getCategories(): Resource<CategoriesResponse>
    suspend fun  getRestaurants(lat: Double, lon: Double): Resource<RestaurantResponse>
}