package com.ajverma.jetfoodapp.data.respository

import android.util.Log
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.address.ReverseGeocodeRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddressListResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.domain.repositories.AddressListRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddressListRepositoryImpl @Inject constructor(
    private val api: FoodApi
): AddressListRepository {
    override suspend fun getAddressList(): Resource<AddressListResponse> {
        return try {
            val response = api.getAddressList()
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

    override suspend fun reverseGeocode(lat: Double, lon: Double): Resource<Address> {
        return try {
            val response = api.reverseGeocode(
                ReverseGeocodeRequest(
                    latitude = lat,
                    longitude = lon
                )
            )
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

    override suspend fun storeAddress(address: Address): Resource<GenericMsgResponse> {
        return try {
            val response = api.storeAddress(address)
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