package com.ajverma.jetfoodapp.presentation.screens.address_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajverma.jetfoodapp.R
import com.ajverma.jetfoodapp.presentation.screens.cart.AddressCard
import com.ajverma.jetfoodapp.presentation.screens.navigation.AddAddress
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is AddressListViewModel.AddressEvent.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }

                is AddressListViewModel.AddressEvent.NavigateToEditAddress -> {
                    TODO()
                }

                is AddressListViewModel.AddressEvent.NavigateBack -> {
                    val address = it.address
                    navController.previousBackStackEntry?.savedStateHandle?.set("address", address)
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }

    val isAddressAdded =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("isAddressAdded", false)
            ?.collectAsState(false)
    LaunchedEffect(key1 = isAddressAdded?.value) {
        if (isAddressAdded?.value == true) {
            viewModel.getAddress()
        }

    }

    Column(Modifier.fillMaxSize()) {
        Row (
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
            Text(text = "Address List", style = MaterialTheme.typography.titleMedium)
            Image(
                painter = painterResource(R.drawable.add),
                contentDescription = null,
                modifier = Modifier.clickable {
                    viewModel.onAddAddressClicked()
                }
            )
        }

        when (uiState.value) {

            AddressListViewModel.AddressState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is AddressListViewModel.AddressState.Success -> {
                val addresses = uiState.value as AddressListViewModel.AddressState.Success
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(addresses.data){ address ->
                        AddressCard(address = address, onAddressClicked = {
                            viewModel.onAddressSelected(address)
                        })
                    }
                }
            }

            is AddressListViewModel.AddressState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = (uiState.value as AddressListViewModel.AddressState.Error).message,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Button(onClick = { viewModel.getAddress() }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }





}