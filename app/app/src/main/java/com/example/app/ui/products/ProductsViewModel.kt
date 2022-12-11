package com.example.app.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.dao.Database
import com.example.app.models.Product

class ProductsViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>().apply {
        value = Database.getProducts()
    }
    val products: LiveData<List<Product>> = _products
}
