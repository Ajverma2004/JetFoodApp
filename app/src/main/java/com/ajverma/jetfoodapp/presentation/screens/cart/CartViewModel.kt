package com.ajverma.jetfoodapp.presentation.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.Address
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartItem
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.cart.CartResponse
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.ConfirmPaymentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentRequest
import com.ajverma.jetfoodapp.data.network.models.restaurantModels.payments.PaymentIntentResponse
import com.ajverma.jetfoodapp.domain.repositories.CartRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<CartState>(CartState.Nothing)
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CartNavigationEvent>()
    val navigationEvent: SharedFlow<CartNavigationEvent> = _navigationEvent.asSharedFlow()

    var cart: CartResponse? = null

    var errorTitle: String = ""
    var errorMessage: String = ""

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address: StateFlow<Address?> = _address.asStateFlow()

    private var paymentId: PaymentIntentResponse? = null


    init {
        getCart()
    }

    fun getCart(){
        viewModelScope.launch {
            _uiState.value = CartState.Loading

            val res = cartRepository.getCart()
            when(res){
                is Resource.Success -> {
                    cart = res.data
                    _cartItemCount.value = res.data.items.size
                    _uiState.value = CartState.Success(res.data)
                }
                is Resource.Error -> {
                    _uiState.value = CartState.Error(res.message)
                    _navigationEvent.emit(CartNavigationEvent.ShowErrorDialog)
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem){
        if (cartItem.quantity > 10){
            return
        }
        updateCart(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem){
        if (cartItem.quantity == 1){
            return
        }
        updateCart(cartItem, cartItem.quantity - 1)
    }

    private fun updateCart(cartItem: CartItem, quantity: Int){
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val res = cartRepository.updateCart(cartItem.id, quantity)

                when(res){
                    is Resource.Success -> {
                        getCart()
                    }
                    is Resource.Error -> {
                        errorTitle = "Cannot Update Quantity"
                        errorMessage = "An error occurred while updating the quantity"
                        cart?.let {
                            _uiState.value = CartState.Success(cart!!)
                        }
                        _navigationEvent.emit(CartNavigationEvent.OnQuantityUpdateError)
                    }
                }
            } catch (e: Exception){
                errorTitle = "Cannot Update Quantity"
                errorMessage = "An error occurred while updating the quantity"

                Log.e("CartViewModel", "updateCart: $e", e)
                cart?.let {
                    _uiState.value = CartState.Success(cart!!)
                }
                _navigationEvent.emit(CartNavigationEvent.OnQuantityUpdateError)
            }
        }
    }

    fun removeFromCart(cartItem: CartItem){
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val res = cartRepository.removeFromCart(cartItem.id)

                when(res){
                    is Resource.Success -> {
                        getCart()
                    }
                    is Resource.Error -> {
                        errorTitle = "Cannot Delete Item"
                        errorMessage = "An error occurred while deleting the item"
                        cart?.let {
                            _uiState.value = CartState.Success(cart!!)
                        }
                        _navigationEvent.emit(CartNavigationEvent.OnItemRemoveError)
                    }
                }
            } catch (e: Exception){

                errorTitle = "Cannot Delete Item"
                errorMessage = "An error occurred while deleting the item"

                Log.e("CartViewModel", "updateCart: $e", e)
                cart?.let {
                    _uiState.value = CartState.Success(cart!!)
                }
                _navigationEvent.emit(CartNavigationEvent.OnItemRemoveError)
            }
        }
    }

    fun checkout(){
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val res = cartRepository.getPaymentIntent(address.value!!.id!!)

                when(res){
                    is Resource.Success -> {
                        paymentId = res.data
                        _navigationEvent.emit(CartNavigationEvent.OnInitiatePayment(res.data))
                        _uiState.value = CartState.Success(cart!!)
                    }
                    is Resource.Error -> {
                        Log.e("CartViewModel", "checkout: ${res.message}", )
                        errorTitle = "Cannot Checkout"
                        errorMessage = "An error occurred while checking out"
                        _navigationEvent.emit(CartNavigationEvent.ShowErrorDialog)
                        _uiState.value = CartState.Success(cart!!)
                    }

                }
            } catch (e: Exception){
                Log.e("CartViewModel", "checkout: $e", e)
                errorTitle = "Cannot Checkout"
                errorMessage = "An error occurred while checking out"
                _navigationEvent.emit(CartNavigationEvent.ShowErrorDialog)
                _uiState.value = CartState.Success(cart!!)
            }
        }
    }

    fun onAddressClick() {
        viewModelScope.launch {
            _navigationEvent.emit(CartNavigationEvent.OnAddressClick)
        }
    }

    fun onAddressSelected(it: Address) {
        _address.value = it
    }

    fun onPaymentSuccess(){
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val res = cartRepository.verifyPayment(address.value!!.id!!, paymentId!!.paymentIntentId)

                when(res){
                    is Resource.Success -> {
                        _navigationEvent.emit(CartNavigationEvent.OrderSuccess(res.data.orderId))
                        _uiState.value = CartState.Success(cart!!)
                        getCart()
                    }

                    is Resource.Error -> {
                        Log.e("CartViewModel", "onPaymentSuccess: ${res.message}", )
                        errorTitle = "Payment Failed"
                        errorMessage = "An error occurred while processing your payment"
                        _navigationEvent.emit(CartNavigationEvent.ShowErrorDialog)
                        _uiState.value = CartState.Success(cart!!)
                    }
                }
            } catch (e: Exception){
                Log.e("CartViewModel", "onPaymentSuccess: $e", e)
                errorTitle = "Payment Failed"
                errorMessage = "An error occurred while processing your payment"
                _navigationEvent.emit(CartNavigationEvent.ShowErrorDialog)
                _uiState.value = CartState.Success(cart!!)
            }
        }
    }

    fun onPaymentFailed() {
        errorTitle = "Payment Failed"
        errorMessage = "We were unable to process your payment. Please try again."
        viewModelScope.launch {
            _navigationEvent.tryEmit(CartNavigationEvent.ShowErrorDialog)
        }
    }


    sealed class CartState {
        data object Nothing: CartState()
        data object Loading: CartState()
        data class Success(val cart: CartResponse): CartState()
        data class Error(val message: String): CartState()
    }

    sealed class CartNavigationEvent {
        data class OrderSuccess(val orderId: String?): CartNavigationEvent()
        data object GoBack: CartNavigationEvent()
        data class OnInitiatePayment(val data: PaymentIntentResponse): CartNavigationEvent()
        data object ShowErrorDialog: CartNavigationEvent()
        data object OnCheckout: CartNavigationEvent()
        data object OnQuantityUpdateError: CartNavigationEvent()
        data object OnItemRemoveError: CartNavigationEvent()
        data object OnAddressClick: CartNavigationEvent()
    }
}