package com.ajverma.jetfoodapp.data.network.models.authModels

data class SignUpRequest (
    val name: String,
    val email: String,
    val password: String
)