package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.Order
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.OrderListResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface OrderRepository {
    suspend fun getOrders(): Resource<OrderListResponse>
    suspend fun getOrderDetails(orderId: String): Resource<Order>

}