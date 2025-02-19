package com.ajverma.jetfoodapp.presentation.screens.notifications

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.notificationModels.Notification
import com.ajverma.jetfoodapp.presentation.screens.home.NotificationPermissionRequest
import com.ajverma.jetfoodapp.presentation.screens.navigation.OrderDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        if (
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ){
            viewModel.getNotifications()
        }
    }

    val uiState = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest {
            when(it){
                is NotificationsViewModel.NotificationEvent.NavigateToOrderDetails -> {
                    navController.navigate(OrderDetails(it.orderId))
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        NotificationHeader(onBackClicked = { navController.popBackStack() })

        if (
            (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            ){
            when(val data = uiState.value){
                is NotificationsViewModel.NotificationState.Loading -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                is NotificationsViewModel.NotificationState.Error -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = data.message,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.getNotifications() }
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }


                is NotificationsViewModel.NotificationState.Success -> {
                    val notifications = data.data

                    LazyColumn {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(notification, onRead = { viewModel.readNotification(notification) })
                        }
                    }
                }
            }
        } else{
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Notification permission is required to show notifications.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    openAppSettings(context)
                }) {
                    Text(text = "Open Settings")
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onRead: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            .clickable {
                onRead()
            }
            .padding(10.dp)
    ) {
        Text(
            text = notification.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun NotificationHeader(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
    Row (
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = null,
            modifier = Modifier
                .shadow(12.dp, clip = true, shape = CircleShape)
                .clickable { onBackClicked.invoke() }
        )
        Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(48.dp))
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}