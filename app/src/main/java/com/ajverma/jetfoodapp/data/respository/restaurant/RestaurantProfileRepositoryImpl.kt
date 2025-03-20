package com.ajverma.jetfoodapp.data.respository.restaurant

import android.util.Log
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddressListResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.Restaurant
import com.ajverma.jetfoodapp.domain.repositories.restaurant.RestaurantProfileRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RestaurantProfileRepositoryImpl @Inject constructor(
    private val api: FoodApi
): RestaurantProfileRepository {
    override suspend fun getRestaurantProfile(): Resource<Restaurant> {
            return try {
                val response = api.getRestaurantProfile()
                Log.d("AddressListRepositoryImpl", "getAddressList: $response")
                Resource.Success(response)
            } catch (e: HttpException) {
                // Map HTTP exceptions to domain-specific errors
                Log.d("AddressListRepositoryImpl", "getAddressList: ${e.code()}")
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
                Log.d("AddressListRepositoryImpl", "getAddressList: ${e.message}")

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