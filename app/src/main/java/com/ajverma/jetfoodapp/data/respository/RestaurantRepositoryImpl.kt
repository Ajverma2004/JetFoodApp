package com.ajverma.jetfoodapp.data.respository

import android.util.Log
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItemResponse
import com.ajverma.jetfoodapp.domain.repositories.RestaurantRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi
): RestaurantRepository {
    override suspend fun getRestaurantMenu(restaurantId: String): Resource<FoodItemResponse> {
        return try {
                // Call the API
                val response = foodApi.getRestaurantMenu(restaurantId)
                Log.d("RestaurantRepository", "Response: ${response.foodItems}")
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
