package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.authModels.AuthResponse
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import com.ajverma.jetfoodapp.domain.utils.Resource

interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Resource<AuthResponse>
    suspend fun signIn(request: SignInRequest): Resource<AuthResponse>
    suspend fun oAuth(request: OAuthRequest): Resource<AuthResponse>
}