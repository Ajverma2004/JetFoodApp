package com.ajverma.jetfoodapp.data.respository

import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.CategoriesResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.RestaurantResponse
import com.ajverma.jetfoodapp.domain.repositories.HomeRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi
) : HomeRepository {
    override suspend fun getCategories(): Resource<CategoriesResponse> {
        return try {
                // Call the API
                val response = foodApi.getCategories()
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


    override suspend fun getRestaurants(
        lat: Double,
        lon: Double
    ): Resource<RestaurantResponse> {
        return try {
                // Call the API
                val response = foodApi.getRestaurants(lat,lon)
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
