package com.ajverma.jetfoodapp.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.Category
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.domain.repositories.HomeRepository
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
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    var restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    init {
        viewModelScope.launch {
            getCategories()
            getPopularRestaurants()
        }
    }

    private suspend fun getCategories(){
        _uiState.value = HomeState.Loading
        val result = homeRepository.getCategories()

        when(result){
            is Resource.Success -> {
                _uiState.value = HomeState.Success
                _categories.value = result.data.data
            }
            else -> {}
        }
    }

    private suspend fun getPopularRestaurants() {
        _uiState.value = HomeState.Loading
        val result = homeRepository.getRestaurants(40.7128, -74.0060)

        when(result){
            is Resource.Success -> {
                _restaurants.value = result.data.data
                _uiState.value = HomeState.Success
            }
            else -> {}
        }
    }

    fun navigateToRestaurant(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeNavigationEvent.NavigateToDetail(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.imageUrl
                )
            )
        }
    }


    sealed class HomeState{
        data object Loading: HomeState()
        data object Success: HomeState()
        data class Error(val message: String): HomeState()
    }

    sealed class HomeNavigationEvent{
        data class NavigateToDetail(
            val id: String,
            val name: String,
            val imageUrl: String
        ): HomeNavigationEvent()

    }
}