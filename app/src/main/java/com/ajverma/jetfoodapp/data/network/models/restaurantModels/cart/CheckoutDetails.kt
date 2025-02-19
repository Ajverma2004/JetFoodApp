package com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart

data class CheckoutDetails(
    val deliveryFee: Double,
    val subTotal: Double,
    val tax: Double,
    val totalAmount: Double
)