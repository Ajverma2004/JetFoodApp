package com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val orderId: String,
    val quantity: Int
)
