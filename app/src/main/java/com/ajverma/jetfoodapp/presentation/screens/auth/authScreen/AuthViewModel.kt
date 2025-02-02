package com.ajverma.jetfoodapp.presentation.screens.auth.authScreen

import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import com.ajverma.jetfoodapp.presentation.screens.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val session: JetFoodSession
) : BaseAuthViewModel(authRepository) {

    private val _uiState = MutableStateFlow<AuthEvent>(AuthEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()



    fun onPasswordChange(password: String){
        _password.value = password
    }

    fun onEmailChange(email: String){
        _email.value = email
    }


    sealed class AuthNavigationEvent{
        data object NavigateToLogin: AuthNavigationEvent()
        data object NavigateToHome: AuthNavigationEvent()
        data object ShowDialog: AuthNavigationEvent()
    }

    sealed class AuthEvent{
        data object Nothing: AuthEvent()
        data object Success: AuthEvent()
        data class Error(val message: String?): AuthEvent()
        data object Loading: AuthEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = AuthEvent.Loading
        }
    }

    override fun onGoogleError(message: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            _uiState.value = AuthEvent.Error(message)
            _navigationEvent.emit(AuthNavigationEvent.ShowDialog)
        }
    }

    override fun onFacebookError(message: String) {
        viewModelScope.launch {
            error = "Facebook Sign In Failed"
            _uiState.value = AuthEvent.Error(message)
            _navigationEvent.emit(AuthNavigationEvent.ShowDialog)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.saveToken(token)
            _uiState.value = AuthEvent.Success
            _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
        }
    }
}