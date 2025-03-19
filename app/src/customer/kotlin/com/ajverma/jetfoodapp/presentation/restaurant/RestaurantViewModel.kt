package com.ajverma.jetfoodapp.presentation.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItem
import com.ajverma.jetfoodapp.domain.repositories.RestaurantRepository
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
class RestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
): ViewModel() {

    var error = ""

    private val _uiState = MutableStateFlow<RestaurantState>(RestaurantState.Nothing)
    val uiState: StateFlow<RestaurantState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent: SharedFlow<RestaurantNavigationEvent> = _navigationEvent.asSharedFlow()




    fun getRestaurantMenu(restaurantId: String){
        viewModelScope.launch {
            _uiState.value = RestaurantState.Loading
            try {
                val response = restaurantRepository.getRestaurantMenu(restaurantId)

                when (response){
                    is Resource.Success -> {
                        _uiState.value = RestaurantState.Success(response.data.foodItems)
                    }
                    is Resource.Error -> {
                        error = "Error"
                        _uiState.value = RestaurantState.Error(response.message)
                        _navigationEvent.emit(RestaurantNavigationEvent.GoBack)
                        _navigationEvent.emit(RestaurantNavigationEvent.ShowDialog)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
                error = "Error"
                _uiState.value = RestaurantState.Error(e.message ?: "Unknown error")
                _navigationEvent.emit(RestaurantNavigationEvent.ShowDialog)
            }
        }
    }

    sealed class RestaurantNavigationEvent{
        data object GoBack: RestaurantNavigationEvent()
        data object ShowDialog: RestaurantNavigationEvent()
        data class NavigateToProductDetails(val restaurantId: String): RestaurantNavigationEvent()
    }
    sealed class RestaurantState {
        data object Nothing : RestaurantState()
        data object Loading : RestaurantState()
        data class Success(val foodItems: List<FoodItem>) : RestaurantState()
        data class Error(val message: String) : RestaurantState()
    }
}