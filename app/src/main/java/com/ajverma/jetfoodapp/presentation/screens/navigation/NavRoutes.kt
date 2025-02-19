package com.ajverma.jetfoodapp.presentation.screens.navigation

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import kotlinx.serialization.Serializable

interface NavRoutes

@Serializable
object Home: NavRoutes

@Serializable
data class RestaurantDetails(
    val id: String,
    val name: String,
    val imageUrl: String,
): NavRoutes

@Serializable
data class FoodItemDetails(
    val foodItem: FoodItem
): NavRoutes

@Serializable
object Notification: NavRoutes

@Serializable
object AddAddress: NavRoutes

@Serializable
data class OrderStatus(
    val orderId: String
): NavRoutes

@Serializable
data class OrderDetails(
    val orderId: String
): NavRoutes

@Serializable
object OrderList: NavRoutes

@Serializable
object AddressList: NavRoutes

@Serializable
object Cart: NavRoutes

@Serializable
object SignUp: NavRoutes

@Serializable
object Login: NavRoutes

@Serializable
object AuthOption: NavRoutes

