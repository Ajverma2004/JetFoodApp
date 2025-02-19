package com.ajverma.jetfoodapp.domain.repositories

import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddressListResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.domain.utils.Resource

interface AddressListRepository {
    suspend fun getAddressList(): Resource<AddressListResponse>
    suspend fun reverseGeocode(lat: Double, lon: Double): Resource<Address>
    suspend fun storeAddress(address: Address): Resource<GenericMsgResponse>
}