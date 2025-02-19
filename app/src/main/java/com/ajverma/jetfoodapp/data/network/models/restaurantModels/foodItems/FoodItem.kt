package com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String?,
    val createdAt: String,
    val description: String,
    val id: String,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val restaurantId: String,
)
