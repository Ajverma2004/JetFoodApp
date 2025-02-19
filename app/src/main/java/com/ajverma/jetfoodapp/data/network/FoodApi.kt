package com.ajverma.jetfoodapp.data.network

import com.ajverma.jetfoodapp.data.network.models.authModels.AuthResponse
import com.ajverma.jetfoodapp.data.network.models.authModels.OAuthRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignInRequest
import com.ajverma.jetfoodapp.data.network.models.authModels.SignUpRequest
import com.ajverma.jetfoodapp.data.network.models.notificationModels.FCMRequest
import com.ajverma.jetfoodapp.data.network.models.notificationModels.NotificationResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.address.ReverseGeocodeRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddToCartRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddToCartResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.AddressListResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.GenericMsgResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.UpdateCartRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.categories.CategoriesResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.foodItems.FoodItemResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.Order
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.orders.OrderListResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.restaurants.RestaurantResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {

    @GET("categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): RestaurantResponse

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): FoodItemResponse

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): AddToCartResponse

    @GET("cart")
    suspend fun getCart(): CartResponse

    @PATCH("cart")
    suspend fun updateCart(@Body request: UpdateCartRequest): GenericMsgResponse

    @DELETE("cart/{cartItemId}")
    suspend fun removeFromCart(@Path("cartItemId") cartItemId: String): GenericMsgResponse

    @GET("addresses")
    suspend fun getAddressList(): AddressListResponse

    @POST("addresses/reverse-geocode")
    suspend fun reverseGeocode(@Body request: ReverseGeocodeRequest): Address

    @POST("/addresses")
    suspend fun storeAddress(@Body address: Address): GenericMsgResponse

    @POST("payments/create-intent")
    suspend fun getPaymentIntent(@Body request: PaymentIntentRequest): PaymentIntentResponse

    @POST("payments/confirm/{paymentIntentId}")
    suspend fun verifyPayment(
        @Body request: ConfirmPaymentRequest,
        @Path("paymentIntentId") paymentIntentId: String
    ): ConfirmPaymentResponse

    @GET("orders")
    suspend fun getOrders(): OrderListResponse

    @GET("orders/{orderId}")
    suspend fun getOrderDetails(
        @Path("orderId") orderId: String
    ): Order

    @PUT("notifications/fcm-token")
    suspend fun updateToken(@Body request: FCMRequest): GenericMsgResponse

    @POST("notifications/{notificationId}/read")
    suspend fun readNotification(@Path("notificationId") notificationId: String): GenericMsgResponse

    @GET("notifications")
    suspend fun getNotifications(): NotificationResponse

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("auth/login")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): AuthResponse


}