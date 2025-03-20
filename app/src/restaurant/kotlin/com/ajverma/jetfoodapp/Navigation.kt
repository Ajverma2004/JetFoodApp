package com.ajverma.jetfoodapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import com.ajverma.jetfoodapp.presentation.screens.auth.authScreen.AuthScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpScreen
import com.ajverma.jetfoodapp.presentation.screens.navigation.AuthOption
import com.ajverma.jetfoodapp.presentation.screens.navigation.Cart
import com.ajverma.jetfoodapp.presentation.screens.navigation.FoodItemDetails
import com.ajverma.jetfoodapp.presentation.screens.navigation.Home
import com.ajverma.jetfoodapp.presentation.screens.navigation.Login
import com.ajverma.jetfoodapp.presentation.screens.navigation.Notification
import com.ajverma.jetfoodapp.presentation.screens.navigation.RestaurantDetails
import com.ajverma.jetfoodapp.presentation.screens.navigation.SignUp
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsScreen
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsViewModel
import com.ajverma.jetfoodapp.presentation.utils.widgets.JetFoodNavHost
import kotlin.reflect.typeOf

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onScreenChanged: (Boolean) -> Unit,
    notificationsViewModel: NotificationsViewModel,
) {
    val session = JetFoodSession(LocalContext.current)

    SharedTransitionLayout {
        JetFoodNavHost(
            navController = navController,
            startDestination = if (session.getToken() != null) Home else AuthOption,
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
                    isCostumer = false
                )
            }
            composable<Home> {
                onScreenChanged(true)
                RestaurantHomeScreen(
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }

            composable<AuthOption> {
                onScreenChanged(false)
                AuthScreen(
                    navController = navController,
                    isCostumer = false
                )
            }


            composable<Notification>{
                SideEffect {
                    onScreenChanged(true)
                }
                NotificationsScreen(navController = navController, viewModel = notificationsViewModel)
            }
        }
    }
}

@Composable
fun RestaurantHomeScreen(
    navController: NavHostController,
    animatedVisibilityScope: AnimatedContentScope,
) {
    Text("home")
}
