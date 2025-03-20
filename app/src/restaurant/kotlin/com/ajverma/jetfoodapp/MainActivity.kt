package com.ajverma.jetfoodapp

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ajverma.jetfoodapp.presentation.screens.navigation.NavRoutes
import com.ajverma.jetfoodapp.presentation.screens.navigation.Notification
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsViewModel
import com.ajverma.jetfoodapp.ui.theme.JetFoodAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var showSplashScreen = true
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0.0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0.0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val notificationViewModel = hiltViewModel<NotificationsViewModel>()
            JetFoodAppTheme {
                val context = this

                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Notification,
                    BottomNavItem.OrderList
                )
                val navController = rememberNavController()
                var showBottomNav by rememberSaveable { mutableStateOf(false) }
                val unreadCount = notificationViewModel.unreadCount.collectAsStateWithLifecycle()



                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = showBottomNav) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                navItems.forEach{ item->
                                    val selected = currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true

                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route)
                                        },
                                        icon = {
                                            Box(modifier = Modifier.size(48.dp)){
                                                Icon(
                                                    painter = painterResource(id = item.icon),
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                                if(
                                                    context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                                ){
                                                    if(item.route == Notification && unreadCount.value > 0) {
                                                        ItemCount(unreadCount.value)
                                                    }
                                                }
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize() 
                            .padding(innerPadding)
                            .background(Color.White)
                    ) {
                        Navigation(
                            navController = navController,
                            onScreenChanged = {showBottomNav = it},
                            notificationsViewModel = notificationViewModel
                        )
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            showSplashScreen = false
        }

    }

    sealed class BottomNavItem(val route: NavRoutes, val icon: Int){
        data object Home: BottomNavItem(com.ajverma.jetfoodapp.presentation.screens.navigation.Home, R.drawable.ic_home)
        data object Cart: BottomNavItem(com.ajverma.jetfoodapp.presentation.screens.navigation.Cart, R.drawable.cart)
        data object Notification: BottomNavItem(com.ajverma.jetfoodapp.presentation.screens.navigation.Notification, R.drawable.ic_notification)
        data object OrderList: BottomNavItem(com.ajverma.jetfoodapp.presentation.screens.navigation.OrderList, R.drawable.ic_orders)
    }
}

@Composable
fun BoxScope.ItemCount(count: Int) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFC529))
            .align(Alignment.TopEnd),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$count",
            color = Color.White,
            fontSize = 10.sp
        )
    }
}
