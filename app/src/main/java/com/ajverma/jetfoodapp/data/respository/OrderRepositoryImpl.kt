package com.ajverma.jetfoodapp.data.respository

import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddToCartRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.Order
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.OrderListResponse
import com.ajverma.jetfoodapp.domain.repositories.OrderRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: FoodApi
): OrderRepository {
    override suspend fun getOrders(): Resource<OrderListResponse> {
        return try {
            val response = api.getOrders()
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

    override suspend fun getOrderDetails(orderId: String): Resource<Order> {
        return try {
            val response = api.getOrderDetails(orderId)
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