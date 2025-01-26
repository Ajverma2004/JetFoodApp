package com.ajverma.jetfoodapp.presentation.screens.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ajverma.jetfoodapp.domain.utils.Resource
import kotlinx.coroutines.delay

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupEvent>(SignupEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignupNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onNameChange(name: String){
        _name.value = name
    }

    fun onPasswordChange(password: String){
        _password.value = password
    }

    fun onEmailChange(email: String){
        _email.value = email
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignupEvent.Loading

            val response = authRepository.signUp(
                SignUpRequest(
                    name = _name.value,
                    email = _email.value,
                    password = _password.value
                )
            )
            when(response){
                is Resource.Success -> {
                    delay(2000)
                    _uiState.value = SignupEvent.Success
                    _navigationEvent.emit(SignupNavigationEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiState.value = SignupEvent.Error(response.message)
                    Log.e("SignUpViewModel", "Error: ${response.message}")
                }
            }
        }
    }


    sealed class SignupNavigationEvent{
        data object NavigateToLogin: SignupNavigationEvent()
        data object NavigateToHome: SignupNavigationEvent()
    }

    sealed class SignupEvent{
        data object Nothing: SignupEvent()
        data object Success: SignupEvent()
        data class Error(val message: String?): SignupEvent()
        data object Loading: SignupEvent()
    }
}