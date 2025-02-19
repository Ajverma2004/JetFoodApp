package com.ajverma.jetfoodapp.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.notificationModels.Notification
import com.ajverma.jetfoodapp.domain.repositories.NotificationRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
): ViewModel() {


    private val _state = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<NotificationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    init {
        getNotifications()
    }

    private fun navigateToOrderDetails(orderId: String){
        viewModelScope.launch {
            _eventFlow.emit(NotificationEvent.NavigateToOrderDetails(orderId))
        }
    }

    fun readNotification(notification: Notification){
        viewModelScope.launch {
            try {
                navigateToOrderDetails(notification.orderId)
                val response = repository.readNotification(notification.id)
                when(response){
                    is Resource.Success -> {
                        getNotifications()
                    }
                    is Resource.Error -> {
                        _state.value = NotificationState.Error("Failed to get notification")
                    }
                }
            } catch (e: Exception){
                _state.value = NotificationState.Error("Failed to get notification")
            }
        }
    }

    fun getNotifications() {
        viewModelScope.launch {
            _state.value = NotificationState.Loading
            try {
                val response = repository.getNotifications()

                when(response){
                    is Resource.Success -> {
                        _unreadCount.value = response.data.unreadCount
                        _state.value = NotificationState.Success(response.data.notifications)
                    }
                    is Resource.Error -> {
                        _state.value = NotificationState.Error("Failed to get notification")
                    }
                }
            } catch (e: Exception){
                _state.value = NotificationState.Error("Failed to get notification")
            }
        }
    }

    // Add this method to handle new notifications
    fun addNewNotification(notification: Notification) {
        val currentNotifications = (_state.value as? NotificationState.Success)?.data ?: emptyList()
        val updatedNotifications = listOf(notification) + currentNotifications
        _state.value = NotificationState.Success(updatedNotifications)
    }

    sealed class NotificationEvent{
        data class NavigateToOrderDetails(val orderId: String): NotificationEvent()
    }

    sealed class NotificationState{
        data object Loading: NotificationState()
        data class Success(val data: List<Notification>): NotificationState()
        data class Error(val message: String): NotificationState()
    }
}