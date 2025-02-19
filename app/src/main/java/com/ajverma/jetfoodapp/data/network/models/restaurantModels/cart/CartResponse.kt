package com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart

data class CartResponse(
    val checkoutDetails: CheckoutDetails,
    val items: List<CartItem>
)