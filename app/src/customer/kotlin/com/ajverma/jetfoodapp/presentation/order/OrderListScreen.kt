@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ajverma.jetfoodapp.presentation.screens.order

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.Order
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.presentation.screens.navigation.OrderDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SharedTransitionScope.OrderListScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderListViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    navController: NavController
) {


    LaunchedEffect(key1 = true){
        viewModel.navigationEvent.collectLatest {
            when(it){
                is OrderListViewModel.OrderNavigationEvent.NavigateToOrderDetailsScreen -> {
                    navController.navigate(OrderDetails(it.order.id))
                }
                is OrderListViewModel.OrderNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }


   Column(horizontalAlignment = Alignment.CenterHorizontally) {

       CartHeader(onBackClicked = { viewModel.navigateBack()})

       val uiState = viewModel.uiState.collectAsStateWithLifecycle()

       when (uiState.value) {
           is OrderListViewModel.OrderListState.Loading -> {
               Box(
                   contentAlignment = Alignment.Center,
                   modifier = modifier.fillMaxSize()
               ) {
                   CircularProgressIndicator(
                       color = MaterialTheme.colorScheme.primary,
                   )
               }
           }
           is OrderListViewModel.OrderListState.Success -> {
               val ordersList = (uiState.value as OrderListViewModel.OrderListState.Success).orders

               if(ordersList.isEmpty()){
                   Column(
                       modifier = modifier.fillMaxSize(),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center
                   ) {
                       Text(text = "No orders found")
                   }
               } else {
                   val listOfTabs = listOf("Upcoming", "History")
                   val coroutineScope = rememberCoroutineScope()
                   val pagerState = rememberPagerState(pageCount = { listOfTabs.size }, initialPage = 0)
                   TabRow(selectedTabIndex = pagerState.currentPage,
                       modifier = Modifier
                           .padding(16.dp)
                           .clip(RoundedCornerShape(32.dp))
                           .border(
                               width = 1.dp,
                               color = Color.LightGray,
                               shape = RoundedCornerShape(32.dp)
                           )
                           .padding(4.dp),
                       indicator = {},
                       divider = {}
                   ) {
                       listOfTabs.forEachIndexed { index, title ->
                           Tab(text = {
                               Text(
                                   text = title,
                                   color = if (pagerState.currentPage == index) Color.White else Color.Gray
                               )
                           }, selected = pagerState.currentPage == index, onClick = {
                               coroutineScope.launch {
                                   pagerState.animateScrollToPage(index)
                               }
                           }, modifier = Modifier
                               .clip(
                                   RoundedCornerShape(32.dp)
                               )
                               .background(
                                   color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.White
                               )
                           )
                       }
                   }

                   HorizontalPager(state = pagerState) {
                       when (it) {
                           0 -> {
                               OrderListInternal(ordersList.filter { order -> order.status == "Pending" },
                                   onClick = { order ->
                                       viewModel.navigateToOrderDetailsScreen(order)
                                   },
                                   animatedVisibilityScope = animatedVisibilityScope
                               )
                           }

                           1 -> {
                               OrderListInternal(ordersList.filter { order -> order.status != "Pending" },
                                   onClick = { order ->
                                       viewModel.navigateToOrderDetailsScreen(order)
                                   },
                                   animatedVisibilityScope = animatedVisibilityScope
                               )
                           }
                       }
                   }
               }
           }
           is OrderListViewModel.OrderListState.Error -> {
               Column(
                   modifier = modifier.fillMaxSize(),
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Center
               ) {
                   Text(text = (uiState.value as OrderListViewModel.OrderListState.Error).message)
                   Spacer(Modifier.height(4.dp))
                   Button(onClick = { viewModel.getOrders() }) {
                       Text(text = "Retry")
                   }
               }
           }
           else -> {}
       }
   }
}



@Composable
fun SharedTransitionScope.OrderListInternal(
    list: List<Order>,
    onClick: (Order) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    if (list.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "No orders found")
        }
    } else {
        LazyColumn {
            items(list) { order ->
                OrderListItem(
                    order = order,
                    onOrderClick = { onClick(order) },
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}


@Composable
fun SharedTransitionScope.OrderListItem(
    order: Order,
    onOrderClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {

            AsyncImage(
                model = order.restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${order.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                ,
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.size(5.dp))

            Column(
                modifier = Modifier.align(Alignment.Bottom).padding(vertical = 10.dp, horizontal = 5.dp)
            ) {
                Text(
                    text = "${order.items.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = order.restaurant.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "#${order.id.subSequence(0,8)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Top)
                    .padding(7.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ){
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onOrderClick,
                modifier = Modifier.padding(10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 30.dp,
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Track Order")
            }
        }
    }
}

@Composable
fun CartHeader(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
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
        Text(text = "Order", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(48.dp))
    }
}

