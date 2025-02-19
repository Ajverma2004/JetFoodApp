package com.ajverma.jetfoodapp.presentation.screens.food_item

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.domain.repositories.FoodItemRepository
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
class FoodItemViewModel @Inject constructor(
    private val foodItemRepository: FoodItemRepository
): ViewModel() {


    private val _uiState = MutableStateFlow<FoodItemState>(FoodItemState.Nothing)
    val uiState: StateFlow<FoodItemState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<FoodItemNavigationEvent>()
    val navigationEvent: SharedFlow<FoodItemNavigationEvent> = _navigationEvent.asSharedFlow()

    private val _quantity = MutableStateFlow<Int>(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun incrementQuantity() {
        _quantity.value++
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value--
        }
    }

    fun addToCart(restaurantId: String, menuItemId: String) {
        viewModelScope.launch {
            _uiState.value = FoodItemState.Loading
            try {
                Log.d("FoodItemViewModel", "Adding to cart - restaurantId: $restaurantId, menuItemId: $menuItemId, quantity: ${quantity.value}")
                
                // Validate inputs
                if (restaurantId.isBlank() || menuItemId.isBlank()) {
                    throw IllegalArgumentException("Invalid restaurant or menu item ID")
                }
                
                val result = foodItemRepository.addToCart(restaurantId, menuItemId, quantity.value)
                Log.d("FoodItemViewModel", "Add to cart result: $result")
                
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = FoodItemState.Nothing
                        _navigationEvent.emit(FoodItemNavigationEvent.OnAddToCart)
                    }
                    is Resource.Error -> {
                        when {
                            result.code == 401 -> {
                                Log.e("FoodItemViewModel", "Authentication error - token might be invalid or expired")
                                _uiState.value = FoodItemState.Error("Session expired. Please log in again.")
                                _navigationEvent.emit(FoodItemNavigationEvent.ShowDialog("Please log in again to continue"))
                            }
                            else -> {
                                Log.e("FoodItemViewModel", "Add to cart error: ${result.message}")
                                _uiState.value = FoodItemState.Error(result.message ?: "Unknown error")
                                _navigationEvent.emit(FoodItemNavigationEvent.ShowDialog(result.message ?: "Unknown error"))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FoodItemViewModel", "Add to cart exception", e)
                _uiState.value = FoodItemState.Error(e.message ?: "Unknown error")
                _navigationEvent.emit(FoodItemNavigationEvent.ShowDialog(e.message ?: "Unknown error"))
            }
        }
    }

    fun goToCart() {
        viewModelScope.launch {
            _navigationEvent.emit(FoodItemNavigationEvent.GoToCart)
        }
    }

    sealed class FoodItemState {
        data object Nothing : FoodItemState()
        data object Loading : FoodItemState()
        data class Error(val message: String) : FoodItemState()
    }

    sealed class FoodItemNavigationEvent {
        data object GoBack : FoodItemNavigationEvent()
        data class ShowDialog(val message: String) : FoodItemNavigationEvent()
        data object OnAddToCart: FoodItemNavigationEvent()
        data object GoToCart : FoodItemNavigationEvent()
    }
}