package com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants

data class Restaurant(
    val id: String,
    val ownerId: String,
    val name: String,
    val address: String,
    val categoryId: String,
    val latitude: Double,
    val imageUrl: String,
    val longitude: Double,
    val createdAt: String,
    val distance: Double? = null,
)
