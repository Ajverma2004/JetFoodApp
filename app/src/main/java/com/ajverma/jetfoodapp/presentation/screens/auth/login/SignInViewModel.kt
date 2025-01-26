package com.ajverma.jetfoodapp.presentation.screens.auth.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.auth.GoogleAuthUiProvider
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
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
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    val googleAuthUiProvider = GoogleAuthUiProvider()


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
                    _uiState.value = SignInEvent.Success
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiState.value = SignInEvent.Error(response.message)
                    Log.e("SignUpViewModel", "Error: ${response.message}")
                }
            }
        }
    }

    fun onGoogleSignInClick(context: Context) {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = googleAuthUiProvider.signIn(
                context,
                CredentialManager.create(context)
            )
            when(response){
                is Resource.Success -> {
                    val request = OAuthRequest(
                        token = response.data.token,
                        provider = "google"
                    )
                    val res = authRepository.oAuth(request)

                    when(res){
                        is Resource.Success -> {
                            Log.d("SignUpViewModel","Success: ${res.data.token}")
                            _uiState.value = SignInEvent.Success
                            _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                        }
                        is Resource.Error -> {
                            _uiState.value = SignInEvent.Error(res.message)
                            Log.e("SignUpViewModel", "Error: ${res.message}")

                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.value = SignInEvent.Error(response.message)
                    Log.e("SignUpViewModel", "Error: ${response.message}")
                }
            }
        }
    }


    sealed class SignInNavigationEvent{
        data object NavigateToSignup: SignInNavigationEvent()
        data object NavigateToHome: SignInNavigationEvent()
    }

    sealed class SignInEvent{
        data object Nothing: SignInEvent()
        data object Success: SignInEvent()
        data class Error(val message: String?): SignInEvent()
        data object Loading: SignInEvent()
    }
}