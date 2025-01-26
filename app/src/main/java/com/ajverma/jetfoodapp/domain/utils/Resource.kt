package com.ajverma.jetfoodapp.domain.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val code: Int? = null, // Optional HTTP status code or error identifier
        val throwable: Throwable? = null // Optional for debugging/logging
    ) : Resource<Nothing>()
}