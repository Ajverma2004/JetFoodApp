package com.ajverma.jetfoodapp.presentation.screens.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class RestaurantDetails(
    val id: String,
    val name: String,
    val imageUrl: String,
)

@Serializable
object SignUp

@Serializable
object Login

@Serializable
object AuthOption

