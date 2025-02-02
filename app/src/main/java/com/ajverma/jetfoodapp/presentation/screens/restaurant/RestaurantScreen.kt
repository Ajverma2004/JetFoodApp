@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ajverma.jetfoodapp.presentation.screens.restaurant

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import com.ajverma.jetfoodapp.presentation.utils.widgets.gridItems
import com.ajverma.jetfoodapp.ui.theme.Orange
import com.ajverma.jetfoodapp.ui.theme.golden

@Composable
fun SharedTransitionScope.RestaurantScreen(
    modifier: Modifier = Modifier,
    viewModel: RestaurantViewModel = hiltViewModel(),
    navController: NavController,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    name: String,
    imageUrl: String,
    ) {

    val uiState = viewModel.uiState.collectAsState()


    LaunchedEffect(restaurantId){
        viewModel.getRestaurantMenu(restaurantId)
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            RestaurantHeader(
                imageUrl = imageUrl,
                onBackClick = { navController.popBackStack() },
                restaurantId = restaurantId,
                animatedVisibilityScope = animatedVisibilityScope,
                onFavoriteClick = {}
            )
        }

        item {
            RestaurantDetails(
                title = name,
                animatedVisibilityScope = animatedVisibilityScope,
                restaurantId = restaurantId,
                description = "lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            )
        }

        when(uiState.value){
            is RestaurantViewModel.RestaurantState.Loading -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is RestaurantViewModel.RestaurantState.Success -> {
                val foodItems = (uiState.value as RestaurantViewModel.RestaurantState.Success).foodItems
                if (foodItems.isNotEmpty()) {
                    gridItems(foodItems, 2) { foodItem ->
                        FoodItemView(footItem = foodItem, onClick = {})
                    }
                } else {
                    item {
                        Text(text = "No Food Items")
                    }
                }
            }
            else -> {}
        }
    }
}


@Composable
fun SharedTransitionScope.RestaurantDetails(
    modifier: Modifier = Modifier,
    restaurantId: String,
    title: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    description: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        //title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "title/${restaurantId}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        )

        //rating
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(golden)
            )
            Text(
                text = "4.5",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = "(69+)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp)
            )
            TextButton(
                onClick = { },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "See Review",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Orange,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        //description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            maxLines = 5,
            color = Color.Gray.copy(alpha = 0.9f)
        )
    }
}


@Composable
private fun SharedTransitionScope.RestaurantHeader(
    modifier: Modifier = Modifier,
    imageUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(30.dp))
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${restaurantId}"),
                        animatedVisibilityScope = animatedVisibilityScope
                )
            ,
            contentScale = ContentScale.Crop
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(32.dp)
                .size(48.dp),
            onClick = onFavoriteClick
        ) {
            Image(painter = painterResource(R.drawable.ic_favourite), contentDescription = null)
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp)
                .size(48.dp),
            onClick = onBackClick
        ) {
            Image(painter = painterResource(R.drawable.ic_back), contentDescription = null)

        }
    }
}

@Composable
fun FoodItemView(
    footItem: FoodItem,
    onClick: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(162.dp)
            .height(216.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .background(Color.White)
            .clickable { onClick.invoke(footItem) }
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(147.dp)
        ) {
            AsyncImage(
                model = footItem.imageUrl, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    ,
                contentScale = ContentScale.Crop,
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Orange)) { // Color for $
                        append("$")
                    }
                    withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) { // Color for price
                        append(footItem.price.toString())
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .align(Alignment.TopStart)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null,
                modifier = Modifier
                    .padding(7.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
            )


            Row(
                modifier = Modifier
                    .padding(bottom = 5.dp, start = 5.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp).padding(start = 2.dp, top = 2.dp, bottom = 2.dp),
                    tint = golden
                )
                Text(
                    text = "(21)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = footItem.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1,
            )
            Text(
                text = footItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}