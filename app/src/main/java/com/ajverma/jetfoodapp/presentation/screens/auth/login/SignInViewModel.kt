package com.ajverma.jetfoodapp.presentation.screens.auth.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import com.ajverma.jetfoodapp.presentation.screens.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val session: JetFoodSession
) : BaseAuthViewModel(authRepository) {

    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
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

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading

            val response = authRepository.signIn(
                SignInRequest(
                    email = _email.value,
                    password = _password.value
                )
            )
            when(response){
                is Resource.Success -> {
                    session.saveToken(response.data.token)
                    _uiState.value = SignInEvent.Success
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    error = "Sign In Failed"
                    _navigationEvent.emit(SignInNavigationEvent.ShowDialog)
                    _uiState.value = SignInEvent.Error(response.message)
                    Log.e("SignUpViewModel", "Error: ${response.message}")
                }
            }
        }
    }


    fun onSignupClick(){
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigateToSignup)
        }
    }


    sealed class SignInNavigationEvent{
        data object NavigateToSignup: SignInNavigationEvent()
        data object NavigateToHome: SignInNavigationEvent()
        data object ShowDialog: SignInNavigationEvent()
    }

    sealed class SignInEvent{
        data object Nothing: SignInEvent()
        data object Success: SignInEvent()
        data class Error(val message: String?): SignInEvent()
        data object Loading: SignInEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
        }
    }

    override fun onGoogleError(message: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            _navigationEvent.emit(SignInNavigationEvent.ShowDialog)
            _uiState.value = SignInEvent.Error(message)
        }
    }

    override fun onFacebookError(message: String) {
        viewModelScope.launch {
            error = "Facebook Sign In Failed"
            _uiState.value = SignInEvent.Error(message)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.saveToken(token)
            _uiState.value = SignInEvent.Success
            _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
        }
    }
}