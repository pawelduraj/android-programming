package com.example.app.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.app.Product

class ProductsViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>().apply {
        value = listOf(
            Product(
                "Gan 13 M MagLev", "3x3x3", 79.99, 1,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "MoYu RS3 M 2020", "3x3x3", 8.99, 3,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "YuXin Little Magic", "3x3x3", 5.99, 3,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "DaYan TengYun M", "3x3x3", 22.99, 3,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "YJ MGC", "4x4x4", 19.99, 4,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "MoYu AoSu WR M", "4x4x4", 43.99, 4,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "YJ MGC", "5x5x5", 22.99, 5,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "YuXin Cloud", "5x5x5", 9.99, 5,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Product(
                "MoYu AoChuang GTS M", "5x5x5", 39.99, 5,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
        )
    }
    val products: LiveData<List<Product>> = _products
}
