package com.ajverma.jetfoodapp.data.respository

import com.ajverma.jetfoodapp.domain.utils.Resource
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.authModels.AuthResponse
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi
) : AuthRepository {

    override suspend fun signUp(request: SignUpRequest): Resource<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Call the API
                 val response = foodApi.signUp(request)
                // Return a success result
                Resource.Success(response)
            } catch (e: HttpException) {
                // Map HTTP exceptions to domain-specific errors
                Resource.Error(
                    message = when (e.code()) {
                        400 -> "Bad request. Please check your input."
                        401 -> "Unauthorized. Please check your credentials."
                        500 -> "Server error. Please try again later."
                        else -> "Unknown error occurred."
                    },
                    code = e.code(),
                    throwable = e
                )
            } catch (e: IOException) {
                // Handle network errors
                Resource.Error(
                    message = "Network error. Please check your connection.",
                    throwable = e
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle any other errors
                Resource.Error(
                    message = "An unexpected error occurred.",
                    throwable = e
                )
            }
        }
    }

    override suspend fun signIn(request: SignInRequest): Resource<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Call the API
                val response = foodApi.signIn(request)
                // Return a success result
                Resource.Success(response)
            } catch (e: HttpException) {
                // Map HTTP exceptions to domain-specific errors
                Resource.Error(
                    message = when (e.code()) {
                        400 -> "Bad request. Please check your input."
                        401 -> "Unauthorized. Please check your credentials."
                        500 -> "Server error. Please try again later."
                        else -> "Unknown error occurred."
                    },
                    code = e.code(),
                    throwable = e
                )
            } catch (e: IOException) {
                // Handle network errors
                Resource.Error(
                    message = "Network error. Please check your connection.",
                    throwable = e
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle any other errors
                Resource.Error(
                    message = "An unexpected error occurred.",
                    throwable = e
                )
            }
        }
    }

    override suspend fun oAuth(request: OAuthRequest): Resource<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Call the API
                val response = foodApi.oAuth(request)
                // Return a success result
                Resource.Success(response)
            } catch (e: HttpException) {
                // Map HTTP exceptions to domain-specific errors
                Resource.Error(
                    message = when (e.code()) {
                        400 -> "Bad request. Please check your input."
                        401 -> "Unauthorized. Please check your credentials."
                        500 -> "Server error. Please try again later."
                        else -> "Unknown error occurred."
                    },
                    code = e.code(),
                    throwable = e
                )
            } catch (e: IOException) {
                // Handle network errors
                Resource.Error(
                    message = "Network error. Please check your connection.",
                    throwable = e
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle any other errors
                Resource.Error(
                    message = "An unexpected error occurred.",
                    throwable = e
                )
            }
        }
    }
}
