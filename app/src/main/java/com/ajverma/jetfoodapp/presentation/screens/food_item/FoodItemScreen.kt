@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.ajverma.jetfoodapp.presentation.screens.food_item

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import com.ajverma.jetfoodapp.presentation.screens.navigation.Cart
import com.ajverma.jetfoodapp.presentation.screens.restaurant.RestaurantDetails
import com.ajverma.jetfoodapp.presentation.screens.restaurant.RestaurantHeader
import com.ajverma.jetfoodapp.presentation.utils.components.BasicDialog
import com.ajverma.jetfoodapp.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodItemScreen(
    foodItem: FoodItem,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodItemViewModel = hiltViewModel(),
    onItemAdded: () -> Unit
) {

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    var showErrorDialog by remember {
        mutableStateOf(false)
    }

    val count  = viewModel.quantity.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isLoading by remember {
        mutableStateOf(false)
    }

    when(uiState) {
        is FoodItemViewModel.FoodItemState.Loading -> {
            isLoading = true
        }
        else -> {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it) {
                is FoodItemViewModel.FoodItemNavigationEvent.GoBack -> {
                    navController.popBackStack()
                }
                is FoodItemViewModel.FoodItemNavigationEvent.ShowDialog -> {
                    showErrorDialog = true
                }
                is FoodItemViewModel.FoodItemNavigationEvent.OnAddToCart -> {
                    onItemAdded.invoke()
                    showSuccessDialog = true
                }
                is FoodItemViewModel.FoodItemNavigationEvent.GoToCart -> {
                    navController.navigate(Cart)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RestaurantHeader(
            imageUrl = foodItem.imageUrl,
            onBackClick = { navController.popBackStack() },
            restaurantId = foodItem.id,
            animatedVisibilityScope = animatedVisibilityScope,
            onFavoriteClick = {}
        )

        RestaurantDetails(
            title = foodItem.name,
            animatedVisibilityScope = animatedVisibilityScope,
            restaurantId = foodItem.id,
            description = foodItem.description
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Orange,
                            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                        )
                    ) {
                        append("$")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Orange,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        )
                    ) {
                        append(foodItem.price.toString())
                    }
                },
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "price/${foodItem.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            ItemQuantityView(
                count = count.value,
                onIncrement = { viewModel.incrementQuantity() },
                onDecrement = { viewModel.decrementQuantity() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.addToCart(
                    restaurantId = foodItem.restaurantId,
                    menuItemId = foodItem.id
                )
            },
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            enabled = !isLoading
        ) {


            AnimatedVisibility(visible = !isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cart),
                        contentDescription = "Cart Icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Cart",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            AnimatedVisibility(visible = isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }

        }
        if (showSuccessDialog) {
            ModalBottomSheet(onDismissRequest = { showSuccessDialog = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Item added to cart", style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.goToCart()
                        }, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Go to Cart")
                    }

                    Button(
                        onClick = {
                            showSuccessDialog = false
                        }, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "OK")
                    }

                }
            }
        }
        if (showErrorDialog) {
            ModalBottomSheet(onDismissRequest = { showSuccessDialog = false }) {
                BasicDialog(
                    title = "Error",
                    description = (uiState as FoodItemViewModel.FoodItemState.Error).message,
                ) {
                    showErrorDialog = false
                }
            }
        }

    }
}

@Composable
fun ItemQuantityView(
    count: Int,
    onIncrement: () -> Unit = {},
    onDecrement: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.remove),
            contentDescription = "Decrease quantity",
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onDecrement.invoke() }
        )


        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.add),
            contentDescription = "Increase quantity",
            modifier = Modifier
                .clip(CircleShape)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = Orange,
                    ambientColor = Orange
                )
                .clickable {
                    onIncrement.invoke()
                }
        )

    }
}
