package com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant

data class Order(
    val address: Address,
    val createdAt: String,
    val id: String,
    val items: List<OrderItem>,
    val paymentStatus: String,
    val restaurant: Restaurant,
    val restaurantId: String,
    val status: String,
    val stripePaymentIntentId: String,
    val totalAmount: Double,
    val updatedAt: String,
    val userId: String
)
