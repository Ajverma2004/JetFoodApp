package com.ajverma.jetfoodapp.domain.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        val code: Int? = null, // Optional HTTP status code or error identifier
        val throwable: Throwable? = null // Optional for debugging/logging
    ) : Result<Nothing>()
}