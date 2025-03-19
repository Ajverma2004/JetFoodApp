package com.ajverma.jetfoodapp.presentation.add_address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.location.LocationManager
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.domain.repositories.AddressListRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val repository: AddressListRepository,
    private val locationManager: LocationManager
): ViewModel() {

    private val _state = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val state: StateFlow<AddAddressState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent?>()
    val event: SharedFlow<AddAddressEvent?> = _event.asSharedFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address: StateFlow<Address?> = _address.asStateFlow()


    fun getLocation() = locationManager.getLocation()

    fun reverseGeocode(lat: Double, lon: Double){
        Log.d("AddAddressViewModel", "reverseGeocode is working")
        viewModelScope.launch {
            _address.value = null
            try {
                val response = repository.reverseGeocode(lat,lon)

                when(response){
                    is Resource.Success -> {
                        _address.value = response.data
                        _state.value = AddAddressState.Success
                    }

                    is Resource.Error -> {
                        _address.value = null
                        _state.value = AddAddressState.Error(response.message)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
                _state.value = AddAddressState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addAddress(address: Address){

    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _state.value = AddAddressState.AddressStoring
            try {

                val response = repository.storeAddress(address.value!!)
                when(response){
                    is Resource.Success -> {
                        _state.value = AddAddressState.Success
                        _event.emit(AddAddressEvent.NavigateToAddressList)
                    }
                    is Resource.Error -> {
                        _state.value = AddAddressState.Error("Failed to store address")
                    }
                }

            } catch (e: Exception){
                e.printStackTrace()
                _state.value = AddAddressState.Error("Failed to store address")
            }
        }
    }

    sealed class AddAddressState{
        data object Loading: AddAddressState()
        data object Success: AddAddressState()
        data object AddressStoring: AddAddressState()
        data class Error(val message: String): AddAddressState()
    }

    sealed class AddAddressEvent{
        data object NavigateToAddressList: AddAddressEvent()
    }

}