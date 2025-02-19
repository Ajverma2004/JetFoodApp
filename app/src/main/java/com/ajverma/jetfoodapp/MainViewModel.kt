package com.ajverma.jetfoodapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _eventFlow = MutableSharedFlow<MainEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onNotificationClick(orderId: String){
        viewModelScope.launch {
            _eventFlow.emit(MainEvent.NavigateToOrderDetails(orderId))
        }
    }

    sealed class MainEvent{
        data class NavigateToOrderDetails(val orderId: String): MainEvent()
    }
}