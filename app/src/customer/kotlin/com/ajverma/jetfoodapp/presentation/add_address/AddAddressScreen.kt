package com.ajverma.jetfoodapp.presentation.add_address

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AddAddressScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {

    val uiState = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when(it){
                is AddAddressViewModel.AddAddressEvent.NavigateToAddressList -> {
                    Toast.makeText(navController.context, "Address added", Toast.LENGTH_SHORT).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "isAddressAdded",
                        true
                    )
                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }


    var isPermissionGranted by remember { mutableStateOf(false) }

    RequestLocationPermission(
        onPermissionGranted = {
            isPermissionGranted = true
            viewModel.getLocation()
        },
        onPermissionDenied = {
            Toast.makeText(navController.context, "Location permission denied", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    )

    if (isPermissionGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            val location = viewModel.getLocation().collectAsStateWithLifecycle(null)
            location.value?.let { loc ->

                val cameraState = rememberCameraPositionState()
                LaunchedEffect(key1 = Unit){
                    cameraState.position = CameraPosition.fromLatLngZoom(LatLng(loc.latitude,loc.longitude),13f)
                }

                var centerScreenMarker by remember {
                    mutableStateOf(LatLng(loc.latitude, loc.longitude))
                }

                LaunchedEffect(key1 = cameraState) {
                    snapshotFlow {
                        cameraState.position.target
                    }.collectLatest {
                        centerScreenMarker = cameraState.position.target
                        viewModel.reverseGeocode(centerScreenMarker.latitude, centerScreenMarker.longitude)

                    }
                }



                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    )
                ){
                    centerScreenMarker.let {
                        Marker(
                            state = MarkerState(
                                position = LatLng(it.latitude, it.longitude)
                            )
                        )
                    }
                }

                val address = viewModel.address.collectAsStateWithLifecycle()
                address.value?.let {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(8.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(Color.White)
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                    ) {

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                if (uiState.value is AddAddressViewModel.AddAddressState.AddressStoring) {
                                    CircularProgressIndicator()
                                } else if (uiState.value is AddAddressViewModel.AddAddressState.Error) {
                                    Text(
                                        text = (uiState.value as AddAddressViewModel.AddAddressState.Error).message,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                } else {
                                    Text(
                                        text = it.addressLine1,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        text = "${it.city}, ${it.state}, ${it.country}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Button(onClick = { viewModel.onAddAddressClicked() }) {
                                Text(text = "Add Address")
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    val context = LocalContext.current
    if (
        (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
        (context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    ) {
        onPermissionGranted()
        return
    }

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions)
    }
}