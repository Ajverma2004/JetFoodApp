package com.ajverma.jetfoodapp.presentation.order_status

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.presentation.screens.navigation.Home

@Composable
fun OrderSuccess(
    orderId: String,
    navController: NavController,
) {

    BackHandler {
        navController.popBackStack(route = Home, inclusive = false)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Order Placed Successfully",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Order ID: $orderId",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Button(
            onClick = { navController.popBackStack(route = Home, inclusive = false) }
        ) {
            Text(text = "Continue Shopping")
        }
    }
}