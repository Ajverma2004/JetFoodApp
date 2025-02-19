package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.notificationModels.NotificationResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface NotificationRepository {
    suspend fun updateToken(token: String): Resource<GenericMsgResponse>
    suspend fun readNotification(notificationId: String): Resource<GenericMsgResponse>
    suspend fun getNotifications(): Resource<NotificationResponse>

}