package com.ajverma.jetfoodapp.presentation.screens.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import com.ajverma.jetfoodapp.presentation.add_address.AddAddressScreen
import com.ajverma.jetfoodapp.presentation.address_list.AddressListScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.authScreen.AuthScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpScreen
import com.ajverma.jetfoodapp.presentation.cart.CartScreen
import com.ajverma.jetfoodapp.presentation.cart.CartViewModel
import com.ajverma.jetfoodapp.presentation.food_item.FoodItemScreen
import com.ajverma.jetfoodapp.presentation.home.HomeScreen
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsScreen
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsViewModel
import com.ajverma.jetfoodapp.presentation.screens.order.OrderListScreen
import com.ajverma.jetfoodapp.presentation.order_detiails.OrderDetailsScreen
import com.ajverma.jetfoodapp.presentation.order_status.OrderSuccess
import com.ajverma.jetfoodapp.presentation.restaurant.RestaurantScreen
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onScreenChanged: (Boolean) -> Unit,
    notificationsViewModel: NotificationsViewModel,
    cartViewModel: CartViewModel
) {
    val session = JetFoodSession(LocalContext.current)

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = if (session.getToken() != null) Home else AuthOption,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            composable<SignUp> {
                onScreenChanged(false)
                SignUpScreen(
                    navController = navController
                )
            }
            composable<Login> {
                onScreenChanged(false)
                SignInScreen(
                    navController = navController,
                )
            }
            composable<Home> {
                onScreenChanged(true)
                HomeScreen(
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }

            composable<AuthOption> {
                onScreenChanged(false)
                AuthScreen(
                    navController = navController
                )
            }

            composable<RestaurantDetails>{
                onScreenChanged(false)
                val route = it.toRoute<RestaurantDetails>()
                RestaurantScreen(
                    navController = navController,
                    restaurantId = route.id,
                    name = route.name,
                    imageUrl = route.imageUrl,
                    animatedVisibilityScope = this
                )
            }

            composable<FoodItemDetails>(
                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
            ){
                onScreenChanged(false)
                val route = it.toRoute<FoodItemDetails>()
                FoodItemScreen(
                    foodItem = route.foodItem,
                    navController = navController,
                    animatedVisibilityScope = this,
                    onItemAdded = {
                        cartViewModel.getCart()
                    }
                )
            }

            composable<Cart>{
                onScreenChanged(true)
                CartScreen(navController = navController, viewModel = cartViewModel)
            }

            composable<Notification>{
                SideEffect {
                    onScreenChanged(true)
                }
                NotificationsScreen(navController = navController, viewModel = notificationsViewModel)
            }

            composable<AddressList> {
                onScreenChanged(false)
                AddressListScreen(navController = navController)
            }

            composable<AddAddress> {
                onScreenChanged(false)
                AddAddressScreen(navController = navController)
            }

            composable<OrderStatus> {
                onScreenChanged(false)
                val orderId = it.toRoute<OrderStatus>().orderId
                OrderSuccess(navController = navController, orderId = orderId)
            }

            composable<OrderList> {
                onScreenChanged(true)
                OrderListScreen(
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }

            composable<OrderDetails> {
                SideEffect {
                    onScreenChanged(false)
                }
                val orderId = it.toRoute<OrderDetails>().orderId
                OrderDetailsScreen(
                    orderId = orderId,
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }
        }
    }
}