package com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart

data class AddToCartRequest(
    val restaurantId: String,
    val menuItemId: String,
    val quantity: Int
)
