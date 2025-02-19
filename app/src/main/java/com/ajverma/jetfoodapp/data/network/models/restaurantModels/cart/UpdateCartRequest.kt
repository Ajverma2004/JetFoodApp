package com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart

data class UpdateCartRequest(
    val cartItemId: String,
    val quantity: Int
)
