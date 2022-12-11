package com.example.app.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.dao.Database
import com.example.app.models.CartProduct

class CartViewModel : ViewModel() {
    private val _cartProducts = MutableLiveData<List<CartProduct>>().apply {
        value = Database.getCartProducts()
    }
    val cartProducts: LiveData<List<CartProduct>> = _cartProducts
}
