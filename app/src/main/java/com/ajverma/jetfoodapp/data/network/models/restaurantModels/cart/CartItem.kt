package com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem

data class CartItem(
    val addedAt: String,
    val id: String,
    val menuItemId: FoodItem,
    val quantity: Int,
    val restaurantId: String,
    val userId: String
)