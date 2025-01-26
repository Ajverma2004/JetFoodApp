package com.ajverma.jetfoodapp.presentation.screens.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ajverma.jetfoodapp.data.respository.AuthRepositoryImpl
import com.ajverma.jetfoodapp.presentation.screens.auth.AuthOptionScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInViewModel
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpViewModel

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthOption,
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Home Screen")
            }
        }

        composable<AuthOption> {
            AuthOptionScreen(
                navController = navController
            )
        }




    }
}