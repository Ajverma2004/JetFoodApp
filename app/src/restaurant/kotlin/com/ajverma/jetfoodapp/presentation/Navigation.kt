package com.ajverma.jetfoodapp.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.presentation.screens.auth.authScreen.AuthScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.login.SignInScreen
import com.ajverma.jetfoodapp.presentation.screens.auth.signup.SignUpScreen
import com.ajverma.jetfoodapp.presentation.screens.home.HomeScreen
import com.ajverma.jetfoodapp.presentation.screens.navigation.AuthOption
import com.ajverma.jetfoodapp.presentation.screens.navigation.Home
import com.ajverma.jetfoodapp.presentation.screens.navigation.Login
import com.ajverma.jetfoodapp.presentation.screens.navigation.Notification
import com.ajverma.jetfoodapp.presentation.screens.navigation.SignUp
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsScreen
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsViewModel
import com.ajverma.jetfoodapp.presentation.utils.widgets.JetFoodNavHost

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
                HomeScreen(navController = navController)
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

