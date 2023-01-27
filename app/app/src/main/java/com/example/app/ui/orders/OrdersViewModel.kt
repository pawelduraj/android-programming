package com.example.app.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.dao.Database
import com.example.app.models.Order

class OrdersViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>().apply {
        value = Database.getOrders()
    }
    val orders: LiveData<List<Order>> = _orders
}
