package com.ajverma.jetfoodapp.presentation.screens.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.auth.GoogleAuthUiProvider
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseAuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    var error: String = ""
    var errDesc: String = ""

    private lateinit var callbackManager: CallbackManager
    private val googleAuthUiProvider = GoogleAuthUiProvider()


    abstract fun loading()
    abstract fun onGoogleError(message: String)
    abstract fun onFacebookError(message: String)
    abstract fun onSocialLoginSuccess(token: String)


    fun onGoogleClick(context: ComponentActivity){
        initiateGoogleLogin(context)
    }

    fun onFacebookClick(context: ComponentActivity){
        initiateFacebookLogin(context)
    }

    private fun initiateFacebookLogin(context: ComponentActivity){
        loading()
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    viewModelScope.launch {
                        val request = OAuthRequest(
                            token = loginResult.accessToken.token,
                            provider = "facebook"
                        )
                        val res = authRepository.oAuth(request)

                        when(res){
                            is Resource.Success -> {
                                Log.d("SignUpViewModel","Success: ${res.data.token}")
                                onSocialLoginSuccess(res.data.token)
                            }
                            is Resource.Error -> {

                                onFacebookError(res.message)
                                Log.e("SignUpViewModel", "Error: ${res.message}")

                            }
                        }
                    }
                }

                override fun onCancel() {
                    onFacebookError("Facebook login canceled")
                }

                override fun onError(exception: FacebookException) {
                    onFacebookError(exception.message.toString())
                    Log.e("SignUpViewModel", "Error: ${exception.message}")
                }
            })

        LoginManager.getInstance().logInWithReadPermissions(
            context,
            callbackManager,
            listOf("public_profile", "email")
        )
    }


    private fun initiateGoogleLogin(context: ComponentActivity){
        viewModelScope.launch {
            loading()
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
                            onSocialLoginSuccess(res.data.token)
                        }
                        is Resource.Error -> {
                            onGoogleError(res.message)
                            Log.e("SignUpViewModel", "Error: ${res.message}")

                        }
                    }
                }
                is Resource.Error -> {
                    onGoogleError(response.message)
                    Log.e("SignUpViewModel", "Error: ${response.message}")
                }
            }
        }
    }
}