package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddToCartResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface FoodItemRepository {
    suspend fun addToCart(restaurantId: String, menuItemId: String, quantity: Int): Resource<AddToCartResponse>
}