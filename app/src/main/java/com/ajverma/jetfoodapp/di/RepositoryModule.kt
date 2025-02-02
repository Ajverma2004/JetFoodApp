package com.ajverma.jetfoodapp.di

import com.ajverma.jetfoodapp.data.respository.AuthRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.HomeRepositoryImpl
import com.ajverma.jetfoodapp.data.respository.RestaurantRepositoryImpl
import com.ajverma.jetfoodapp.domain.repositories.AuthRepository
import com.ajverma.jetfoodapp.domain.repositories.HomeRepository
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


}