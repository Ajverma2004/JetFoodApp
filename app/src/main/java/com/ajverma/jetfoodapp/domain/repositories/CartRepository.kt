package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface CartRepository {
    suspend fun getCart(): Resource<CartResponse>
    suspend fun updateCart(cartItemId: String, quantity: Int): Resource<GenericMsgResponse>
    suspend fun removeFromCart(cartItemId: String): Resource<GenericMsgResponse>
    suspend fun getPaymentIntent(addressId: String): Resource<PaymentIntentResponse>
    suspend fun verifyPayment(addressId: String, paymentIntentId: String): Resource<ConfirmPaymentResponse>


}