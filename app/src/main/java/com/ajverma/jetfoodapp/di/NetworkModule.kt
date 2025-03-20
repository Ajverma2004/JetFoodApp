package com.ajverma.jetfoodapp.di

import android.content.Context
import android.util.Log
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.data.network.auth.JetFoodSession
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.0.32:8080/"
//    private const val BASE_URL = "http://10.0.2.16:8080/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Log body, headers, and more
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, session: JetFoodSession, @ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = session.getToken()
                
                Log.d("NetworkModule", "Adding token to request: $token")
                
                val request = if (!token.isNullOrEmpty()) {
                    val newRequest = original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .header("X-Package-Name", context.packageName)
                        .method(original.method, original.body)
                        .build()
                    
                    // Log the full request headers for debugging
                    Log.d("NetworkModule", "Request URL: ${newRequest.url}")
                    Log.d("NetworkModule", "Authorization Header: ${newRequest.header("Authorization")}")
                    Log.d("NetworkModule", "X-Package-Name Header: ${newRequest.header("X-Package-Name")}")
                    Log.d("NetworkModule", "Request Method: ${context.packageName}")

                    
                    newRequest
                } else {
                    Log.w("NetworkModule", "No token available for request to: ${original.url}")
                    original
                }
                
                // Execute the request and log the response
                val response = chain.proceed(request)
                Log.d("NetworkModule", "Response Code: ${response.code}")
                Log.d("NetworkModule", "Response Message: ${response.message}")
                response
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSession(@ApplicationContext context: Context): JetFoodSession {
        return JetFoodSession(context)
    }

    @Provides
    fun provideLocationService(@ApplicationContext context: Context): FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(context)
    }
}
