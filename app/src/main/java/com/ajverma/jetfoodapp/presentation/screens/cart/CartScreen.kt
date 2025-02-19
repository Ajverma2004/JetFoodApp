package com.ajverma.jetfoodapp.presentation.screens.cart

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartItem
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CheckoutDetails
import com.ajverma.jetfoodapp.domain.utils.StringUtils
import com.ajverma.jetfoodapp.presentation.screens.food_item.FoodItemViewModel
import com.ajverma.jetfoodapp.presentation.screens.food_item.ItemQuantityView
import com.ajverma.jetfoodapp.presentation.screens.navigation.AddressList
import com.ajverma.jetfoodapp.presentation.screens.navigation.OrderStatus
import com.ajverma.jetfoodapp.presentation.screens.order_status.OrderSuccess
import com.ajverma.jetfoodapp.presentation.utils.components.BasicDialog
import com.ajverma.jetfoodapp.ui.theme.Orange
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CartViewModel
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }


    val address = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>("address", null)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let{
            viewModel.onAddressSelected(it)
        }
    }

    val paymentSheet = rememberPaymentSheet(paymentResultCallback = {
        if(it is PaymentSheetResult.Completed){
            viewModel.onPaymentSuccess()
        } else {
            viewModel.onPaymentFailed()
        }
    })

    LaunchedEffect(true){
        viewModel.navigationEvent.collectLatest {
            when(it){
                is CartViewModel.CartNavigationEvent.ShowErrorDialog -> {
                    showDialog = true
                }
                is CartViewModel.CartNavigationEvent.OnQuantityUpdateError -> {}
                is CartViewModel.CartNavigationEvent.OnItemRemoveError -> {}
                is CartViewModel.CartNavigationEvent.OnAddressClick -> {
                    navController.navigate(AddressList)
                }
                is CartViewModel.CartNavigationEvent.OrderSuccess -> {
                    navController.navigate(OrderStatus(it.orderId!!))
                }
                is CartViewModel.CartNavigationEvent.OnInitiatePayment -> {
                    PaymentConfiguration.init(navController.context, it.data.publishableKey)
                    val customer = PaymentSheet.CustomerConfiguration(
                        it.data.customerId,
                        it.data.ephemeralKeySecret
                    )
                    val paymentSheetConfig = PaymentSheet.Configuration(
                        merchantDisplayName = "FoodHub",
                        customer = customer,
                        allowsDelayedPaymentMethods = false,
                    )

                    // Initiate payment

                    paymentSheet.presentWithPaymentIntent(
                        it.data.paymentIntentClientSecret,
                        paymentSheetConfig
                    )
                }
                else -> {}
            }
        }
    }


    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CartHeader(onBackClicked = { navController.popBackStack() })

        when(uiState.value){
            is CartViewModel.CartState.Nothing -> {}
            is CartViewModel.CartState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is CartViewModel.CartState.Success -> {
                val cart = (uiState.value as CartViewModel.CartState.Success).cart

                if (cart.items.isNotEmpty()){
                    LazyColumn {
                        items(cart.items){ cartItem ->
                            CartListItem(cartItem, viewModel)
                        }
                        item {
                            CheckoutView(checkoutDetails = cart.checkoutDetails)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(painter = painterResource(R.drawable.cart), contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Your cart is empty", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }
            is CartViewModel.CartState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = (uiState.value as CartViewModel.CartState.Error).message)
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(onClick = { viewModel.getCart() }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        val selectedAddress = viewModel.address.collectAsStateWithLifecycle()
        if (uiState.value is CartViewModel.CartState.Success){
            AddressCard(selectedAddress.value, onAddressClicked = {
                viewModel.onAddressClick()
            })
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.checkout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                enabled = selectedAddress.value != null
            ) {
                Text(text = "Checkout")
            }
        }
    }

    if (showDialog){
        ModalBottomSheet(onDismissRequest = { showDialog = false }) {
            BasicDialog(
                title = viewModel.errorTitle,
                description = viewModel.errorMessage,
            ) {
                showDialog = false
            }
        }
    }
}


@Composable
fun AddressCard(address: Address?, onAddressClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(8.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(Color.White)
            .clickable { onAddressClicked.invoke() }
            .padding(16.dp)

    ) {
        if (address != null) {
            Column {
                Text(text = address.addressLine1, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${address.city}, ${address.state}, ${address.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            Text(text = "Select Address", style = MaterialTheme.typography.bodyMedium)
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
        Text(text = "Cart", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
fun CartListItem(cartItem: CartItem, viewModel: CartViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(8.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cartItem.menuItemId.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { viewModel.removeFromCart(cartItem) }
                )

            }

            Text(
                text = cartItem.menuItemId.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Gray
            )
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${cartItem.menuItemId.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Orange
                )
                Spacer(Modifier.weight(1f))

                ItemQuantityView(
                    count = cartItem.quantity,
                    onIncrement = { viewModel.incrementQuantity(cartItem) },
                    onDecrement = { viewModel.decrementQuantity(cartItem) }
                )
            }
        }
    }
}

@Composable
fun CheckoutView(checkoutDetails: CheckoutDetails) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        CheckoutRowItem("Sub Total", checkoutDetails.subTotal, "USD")
        CheckoutRowItem("Delivery Fee", checkoutDetails.deliveryFee, "USD")
        CheckoutRowItem("Tax", checkoutDetails.tax, "USD")
        CheckoutRowItem("Total", checkoutDetails.totalAmount, "USD")
    }
}

@Composable
fun CheckoutRowItem(
    title: String,
    value: Double,
    currency: String,
) {
    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = StringUtils.formatCurrency(value),style = MaterialTheme.typography.titleMedium)
            Text(text = currency, style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
        }
        VerticalDivider()
    }
}