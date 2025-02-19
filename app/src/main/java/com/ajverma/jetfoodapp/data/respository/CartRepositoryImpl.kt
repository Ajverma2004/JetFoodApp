package com.ajverma.jetfoodapp.data.respository

import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.UpdateCartRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentResponse
import com.ajverma.jetfoodapp.domain.repositories.CartRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val api: FoodApi
): CartRepository {
    override suspend fun getCart(): Resource<CartResponse> {
        return try {
            val response  = api.getCart()
            Resource.Success(response)
        } catch (e: HttpException) {
            // Map HTTP exceptions to domain-specific errors
            Resource.Error(
                message = when (e.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your credentials."
                    500 -> "Server error. Please try again later."
                    else -> "Unknown error occurred."
                },
                code = e.code(),
                throwable = e
            )
        } catch (e: IOException) {
            // Handle network errors
            Resource.Error(
                message = "Network error. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any other errors
            Resource.Error(
                message = "An unexpected error occurred.",
                throwable = e
            )
        }
    }

    override suspend fun updateCart(cartItemId: String, quantity: Int, ): Resource<GenericMsgResponse> {
        return try {
            val response  = api.updateCart(UpdateCartRequest(cartItemId, quantity))
            Resource.Success(response)
        } catch (e: HttpException) {
            // Map HTTP exceptions to domain-specific errors
            Resource.Error(
                message = when (e.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your credentials."
                    500 -> "Server error. Please try again later."
                    else -> "Unknown error occurred."
                },
                code = e.code(),
                throwable = e
            )
        } catch (e: IOException) {
            // Handle network errors
            Resource.Error(
                message = "Network error. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any other errors
            Resource.Error(
                message = "An unexpected error occurred.",
                throwable = e
            )
        }
    }

    override suspend fun removeFromCart(cartItemId: String): Resource<GenericMsgResponse> {
        return try {
            val response  = api.removeFromCart(cartItemId)
            Resource.Success(response)
        } catch (e: HttpException) {
            // Map HTTP exceptions to domain-specific errors
            Resource.Error(
                message = when (e.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your credentials."
                    500 -> "Server error. Please try again later."
                    else -> "Unknown error occurred."
                },
                code = e.code(),
                throwable = e
            )
        } catch (e: IOException) {
            // Handle network errors
            Resource.Error(
                message = "Network error. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any other errors
            Resource.Error(
                message = "An unexpected error occurred.",
                throwable = e
            )
        }
    }

    override suspend fun getPaymentIntent(addressId: String): Resource<PaymentIntentResponse> {
        return try {
            val response  = api.getPaymentIntent(PaymentIntentRequest(addressId))
            Resource.Success(response)
        } catch (e: HttpException) {
            // Map HTTP exceptions to domain-specific errors
            Resource.Error(
                message = when (e.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your credentials."
                    500 -> "Server error. Please try again later."
                    else -> "Unknown error occurred."
                },
                code = e.code(),
                throwable = e
            )
        } catch (e: IOException) {
            // Handle network errors
            Resource.Error(
                message = "Network error. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any other errors
            Resource.Error(
                message = "An unexpected error occurred.",
                throwable = e
            )
        }
    }

    override suspend fun verifyPayment(
        addressId: String,
        paymentIntentId: String,
    ): Resource<ConfirmPaymentResponse> {
        return try {
            val response  = api.verifyPayment(ConfirmPaymentRequest(addressId, paymentIntentId), paymentIntentId)
            Resource.Success(response)
        } catch (e: HttpException) {
            // Map HTTP exceptions to domain-specific errors
            Resource.Error(
                message = when (e.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your credentials."
                    500 -> "Server error. Please try again later."
                    else -> "Unknown error occurred."
                },
                code = e.code(),
                throwable = e
            )
        } catch (e: IOException) {
            // Handle network errors
            Resource.Error(
                message = "Network error. Please check your connection.",
                throwable = e
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any other errors
            Resource.Error(
                message = "An unexpected error occurred.",
                throwable = e
            )
        }
    }


}