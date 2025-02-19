package com.ajverma.jetfoodapp.di

import com.ajverma.jetfoodapp.data.respository.AddressListRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.AuthRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.CartRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.FoodItemRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.HomeRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.NotificationRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.OrderRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.RestaurantRepositoryImpl
import com.ajverma.jetfoodapp.domain.repositories.AddressListRepository
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import com.ajverma.jetfoodapp.domain.repositories.CartRepository
import com.ajverma.jetfoodapp.domain.repositories.FoodItemRepository
import com.ajverma.jetfoodapp.domain.repositories.HomeRepository
import com.ajverma.jetfoodapp.domain.repositories.NotificationRepository
import com.ajverma.jetfoodapp.domain.repositories.OrderRepository
import com.ajverma.jetfoodapp.domain.repositories.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository


    @Binds
    @Singleton
    abstract fun bindFoodItemRepository(
        foodItemRepositoryImpl: FoodItemRepositoryImpl
    ): FoodItemRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindAddressListRepository(
        addressListRepositoryImpl: AddressListRepositoryImpl
    ): AddressListRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository


}