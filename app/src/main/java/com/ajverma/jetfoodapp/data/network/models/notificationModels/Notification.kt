package com.ajverma.jetfoodapp.data.network.models.notificationModels

data class Notification(
    val createdAt: String,
    val id: String,
    val isRead: Boolean,
    val message: String,
    val orderId: String,
    val title: String,
    val type: String,
    val userId: String
)