package com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments

data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String,
)
