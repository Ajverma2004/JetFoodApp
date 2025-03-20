package com.ajverma.jetfoodapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.domain.repositories.HomeRepository
import com.ajverma.jetfoodapp.domain.repositories.restaurant.RestaurantProfileRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RestaurantProfileRepository
): ViewModel() {

    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state = _state.asStateFlow()

    init {
        getRestaurantProfile()
    }

    fun getRestaurantProfile(){
        viewModelScope.launch {
            _state.value = HomeScreenState.Loading
            try {
                val result = repository.getRestaurantProfile()
                when(result){
                    is Resource.Success -> {
                        _state.value = HomeScreenState.Success(result.data)
                    }
                    is Resource.Error -> {
                        _state.value = HomeScreenState.Error(result.message)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
                _state.value = HomeScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }


    sealed class HomeScreenState{
        data object Loading: HomeScreenState()
        data class Success(val data: Restaurant): HomeScreenState()
        data class Error(val message: String): HomeScreenState()
    }
}