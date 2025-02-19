@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ajverma.jetfoodapp.presentation.screens.order_detiails

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.domain.utils.StringUtils.formatCurrency
import com.ajverma.jetfoodapp.presentation.screens.navigation.OrderDetails
import com.ajverma.jetfoodapp.presentation.screens.order.OrderListViewModel
import com.ajverma.jetfoodapp.presentation.screens.restaurant.RestaurantHeader
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SharedTransitionScope.OrderDetailsScreen(
    modifier: Modifier = Modifier,
    orderId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    navController: NavController
) {


    LaunchedEffect(key1 = true){
        viewModel.navigationEvent.collectLatest {
            when(it){
                is OrderDetailsViewModel.OrderDetailsNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(key1 = orderId) {
        viewModel.getOrderDetails(orderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()

        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsState.Loading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is OrderDetailsViewModel.OrderDetailsState.Success -> {
                val details = (uiState.value as OrderDetailsViewModel.OrderDetailsState.Success).order


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    RestaurantHeader(
                        imageUrl = details.restaurant.imageUrl,
                        onBackClick = { navController.popBackStack() },
                        restaurantId = details.id,
                        onFavoriteClick = {},
                        animatedVisibilityScope = animatedVisibilityScope
                    )



                    Text(
                        text = details.restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${details.id.subSequence(0,8)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.size(7.dp))

                    Text(
                        text = details.address.addressLine1,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(Modifier.size(17.dp))


                    Text(
                        text = "Date: ${details.createdAt}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(15.dp)
                    )


                    Text(
                        text = "Price: ${formatCurrency(details.totalAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(15.dp)
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id =
                                when(details.status){
                                    "Pending" -> R.drawable.ic_pending
                                    "Delivered" -> R.drawable.ic_delivered
                                    "On the Way" -> R.drawable.ic_picked_by_rider
                                    else -> R.drawable.ic_preparing
                            }),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = details.status,
                            style = MaterialTheme.typography.bodyLarge,
                            color = when(details.status){
                                "Pending" -> Color.Yellow
                                "Delivered" -> Color.Green
                                "On the Way" -> Color(0xFF9C27B0)
                                else -> Color.Blue
                            }
                        )
                    }







                }
            }
            is OrderDetailsViewModel.OrderDetailsState.Error -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (uiState.value as OrderDetailsViewModel.OrderDetailsState.Error).message)

                    Button(
                        onClick = { viewModel.getOrderDetails(orderId) }
                    ) {
                        Text(text = "Retry")
                    }
                }

            }
            else -> {}

        }
    }

}
