package com.ajverma.jetfoodapp.presentation.screens.navigation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.presentation.screens.auth.authScreen.AuthScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpScreen
import com.ajverma.jetfoodapp.presentation.screens.home.HomeScreen
import com.ajverma.jetfoodapp.presentation.screens.restaurant.RestaurantScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
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
                SignUpScreen(
                    navController = navController
                )
            }
            composable<Login> {
                SignInScreen(
                    navController = navController,
                )
            }
            composable<Home> {
                HomeScreen(
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }

            composable<AuthOption> {
                AuthScreen(
                    navController = navController
                )
            }

            composable<RestaurantDetails>{
                val route = it.toRoute<RestaurantDetails>()
                RestaurantScreen(
                    navController = navController,
                    restaurantId = route.id,
                    name = route.name,
                    imageUrl = route.imageUrl,
                    animatedVisibilityScope = this
                )
            }


        }
    }
}