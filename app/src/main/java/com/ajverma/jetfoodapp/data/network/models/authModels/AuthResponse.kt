package com.ajverma.jetfoodapp.data.network.models.authModels

data class SignUpResponse (
    val success: Boolean,
    val message: String,
    val data: SignUpResponseData
)