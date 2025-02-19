package com.ajverma.jetfoodapp.data.respository

import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.notificationModels.FCMRequest
import com.ajverma.jetfoodapp.data.network.models.notificationModels.NotificationResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.domain.repositories.NotificationRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi
): NotificationRepository {
    override suspend fun updateToken(token: String): Resource<GenericMsgResponse> {
        return try {
            // Call the API
            val response = foodApi.updateToken(
                FCMRequest(token)
            )
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

    override suspend fun readNotification(notificationId: String): Resource<GenericMsgResponse> {
        return try {
            // Call the API
            val response = foodApi.readNotification(notificationId)
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

    override suspend fun getNotifications(): Resource<NotificationResponse> {
        return try {
            // Call the API
            val response = foodApi.getNotifications()
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