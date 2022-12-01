package com.example.app.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.Cart
import com.example.app.CartProduct

class CartViewModel : ViewModel() {
    private val _cartProducts = MutableLiveData<List<CartProduct>>().apply {
        value = Cart.get()
    }
    val cartProducts: LiveData<List<CartProduct>> = _cartProducts
}
