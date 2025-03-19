package com.ajverma.jetfoodapp.presentation.screens.order

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
class OrderListViewModel @Inject constructor(
    private val repository: OrderRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<OrderListState>(OrderListState.Nothing)
    val uiState: StateFlow<OrderListState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<OrderNavigationEvent>()
    val navigationEvent: SharedFlow<OrderNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        getOrders()
    }

    fun navigateToOrderDetailsScreen(order: Order){
        viewModelScope.launch {
            _navigationEvent.emit(OrderNavigationEvent.NavigateToOrderDetailsScreen(order))
        }
    }

    fun navigateBack(){
        viewModelScope.launch {
            _navigationEvent.emit(OrderNavigationEvent.NavigateBack)
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            _uiState.value = OrderListState.Loading
            try {
                val orders = repository.getOrders()

                when (orders) {
                    is Resource.Success -> {
                        _uiState.value = OrderListState.Success(orders.data.orders)
                    }

                    is Resource.Error -> {
                        _uiState.value = OrderListState.Error(orders.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = OrderListState.Error(e.message ?: "An error occurred")
            }
        }
    }

    sealed class OrderNavigationEvent {
        data class NavigateToOrderDetailsScreen(val order: Order): OrderNavigationEvent()
        data object NavigateBack: OrderNavigationEvent()
    }

    sealed class OrderListState {
        data object Nothing: OrderListState()
        data object Loading: OrderListState()
        data class Success(val orders: List<Order>): OrderListState()
        data class Error(val message: String): OrderListState()

    }
}