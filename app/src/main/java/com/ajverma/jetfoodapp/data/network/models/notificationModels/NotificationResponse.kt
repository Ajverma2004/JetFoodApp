package com.ajverma.jetfoodapp.data.network.models.notificationModels

data class NotificationResponse(
    val notifications: List<Notification>,
    val unreadCount: Int
)