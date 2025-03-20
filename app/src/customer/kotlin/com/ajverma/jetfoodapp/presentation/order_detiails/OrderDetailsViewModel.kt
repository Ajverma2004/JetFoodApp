package com.ajverma.jetfoodapp.presentation.order_detiails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.Order
import com.ajverma.jetfoodapp.domain.repositories.OrderRepository
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
class OrderDetailsViewModel @Inject constructor(
    private val repository: OrderRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Nothing)
    val uiState: StateFlow<OrderDetailsState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<OrderDetailsNavigationEvent>()
    val navigationEvent: SharedFlow<OrderDetailsNavigationEvent> = _navigationEvent.asSharedFlow()




    fun getOrderDetails(orderId: String){
        viewModelScope.launch {
            _uiState.value = OrderDetailsState.Loading
            try {
                val orderDetails = repository.getOrderDetails(orderId)

                when(orderDetails){
                    is Resource.Success -> {
                        _uiState.value = OrderDetailsState.Success(orderDetails.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = OrderDetailsState.Error(orderDetails.message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = OrderDetailsState.Error("An unexpected error occurred.")
            }
        }
    }


    sealed class OrderDetailsState {
        data object Nothing: OrderDetailsState()
        data object Loading: OrderDetailsState()
        data class Success(val order: Order): OrderDetailsState()
        data class Error(val message: String): OrderDetailsState()
    }

    sealed class OrderDetailsNavigationEvent {
        data object NavigateBack: OrderDetailsNavigationEvent()
    }

}