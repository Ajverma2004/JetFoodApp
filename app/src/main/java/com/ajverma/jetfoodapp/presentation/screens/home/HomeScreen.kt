package com.ajverma.jetfoodapp.presentation.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.Category
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.presentation.screens.navigation.RestaurantDetails
import com.ajverma.jetfoodapp.ui.theme.Orange
import com.ajverma.jetfoodapp.ui.theme.Typography
import com.ajverma.jetfoodapp.ui.theme.golden
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()
    val categories = viewModel.categories.collectAsState()
    val restaurants = viewModel.restaurants.collectAsState()


    LaunchedEffect(Unit){
        viewModel.navigationEvent.collectLatest {
            when(it){
                is HomeViewModel.HomeNavigationEvent.NavigateToDetail -> {
                    navController.navigate(RestaurantDetails(id = it.id, name = it.name, imageUrl = it.imageUrl)
                    )
                }
                else -> {}

            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when(uiState.value){
            is HomeViewModel.HomeState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center

                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeViewModel.HomeState.Success -> {
                CategoriesList(
                    categories = categories.value,
                    onCategoryClick = {

                })

                RestaurantsList(
                    restaurants = restaurants.value,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRestaurantClick = {
                        viewModel.navigateToRestaurant(it)
                    }
                )
            }
            is HomeViewModel.HomeState.Error -> {
                Text(text = (uiState.value as HomeViewModel.HomeState.Error).message)
            }
            else -> {}

        }
    }

}

@Composable
fun CategoriesList(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    // Track the selected category
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.id) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category.id == selectedCategory,
                onCategoryClick = {
                    selectedCategory = category.id
                    onCategoryClick(category)
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsList(
    modifier: Modifier = Modifier,
    restaurants: List<Restaurant>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantClick: (Restaurant) -> Unit
) {
    Column{
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Popular Restaurants",
                style = Typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = "View All",
                        style = Typography.bodyMedium,
                        color = Orange

                    )
                }

                Image(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 7.dp).size(20.dp),
                    colorFilter = ColorFilter.tint(Orange)
                )
            }
        }
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(restaurants) { restaurant ->
                RestaurantItem(
                    restaurant = restaurant,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    modifier: Modifier = Modifier,
    restaurant: Restaurant,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantClick: (Restaurant) -> Unit
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .width(250.dp)
            .height(229.dp)
            .shadow(16.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onRestaurantClick(restaurant)
            }

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${restaurant.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                ,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Column(modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
                ) {
                Text(
                    text = restaurant.name,
                    style = Typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "title/${restaurant.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                Row() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delivery),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "Free Delivery",
                            style = Typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.timer),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "Free Delivery",
                            style = Typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .align(TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly

        ) {
            Text(
                text = "4.5", style = Typography.titleSmall,

                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Image(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(golden)
            )
            Text(
                text = "(25+)", style = Typography.bodySmall, color = Color.Gray
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onCategoryClick: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .height(120.dp)
            .width(80.dp)
            .clickable {
                onCategoryClick(category)
            }
            .shadow(
                elevation = if (isSelected) 25.dp else 16.dp,
                shape = RoundedCornerShape(45.dp),
                ambientColor = if (isSelected) Orange else Color.Gray.copy(alpha = 0.8f),
                spotColor = if (isSelected) Orange else Color.Gray.copy(alpha = 0.8f)
            )
            .background(
                color = if (isSelected) Orange else Color.White,
                shape = RoundedCornerShape(45.dp)
            )
            .clip(RoundedCornerShape(45.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = if (isSelected) Orange else Color.Gray.copy(alpha = 0.5f),
                    spotColor = if (isSelected) Orange else Color.Gray.copy(alpha = 0.5f)
                )
                .clip(CircleShape),
            contentScale = ContentScale.Inside
        )


        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = category.name,
            style = TextStyle(
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Black
            ),
            textAlign = TextAlign.Center
        )
    }
}
