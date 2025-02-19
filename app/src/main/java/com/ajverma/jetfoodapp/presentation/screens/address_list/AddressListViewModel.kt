package com.ajverma.jetfoodapp.presentation.screens.address_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AddressListViewModel @Inject constructor(
    private val repository: AddressListRepository
): ViewModel() {

    private val _state = MutableStateFlow<AddressState>(AddressState.Loading)
    val state: StateFlow<AddressState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddressEvent?>()
    val event: SharedFlow<AddressEvent?> = _event.asSharedFlow()

    init {
        getAddress()
    }

    fun getAddress(){
        viewModelScope.launch {
            Log.d("AddressListViewModel", "getAddress() called") // ✅ Add this log

            _state.value = AddressState.Loading
            try {
                val result = repository.getAddressList()
                Log.d("AddressListViewModel", "getAddress: $result") // ✅ Log result

                when(result){
                    is Resource.Success -> {
                        Log.d("AddressListViewModel", "Success: ${result.data}")
                        _state.value = AddressState.Success(result.data.addresses ?: emptyList())
                    }

                    is Resource.Error -> {
                        Log.d("AddressListViewModel", "Error: ${result.message}")
                        _state.value = AddressState.Error(result.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception){
                Log.e("AddressListViewModel", "Exception in getAddress: ${e.message}", e)
                _state.value = AddressState.Error(e.message ?: "Unknown error")
            }
        }
    }


    fun onAddAddressClicked() {
        viewModelScope.launch {
            _event.emit(AddressEvent.NavigateToAddAddress)
        }
    }

    fun onAddressSelected(address: Address) {
        viewModelScope.launch {
            _event.emit(AddressEvent.NavigateBack(address))
        }
    }

    sealed class AddressState{
        data object Loading: AddressState()
        data class Success(val data: List<Address>): AddressState()
        data class Error(val message: String): AddressState()
    }

    sealed class AddressEvent{
        data class NavigateToEditAddress(val address: Address): AddressEvent()
        data object NavigateToAddAddress: AddressEvent()
        data class NavigateBack(val address: Address): AddressEvent()
    }
}