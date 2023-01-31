package com.example.app

import com.example.app.models.CartProduct
import com.example.app.models.Category
import com.example.app.models.*

object Utils {
    private val cart = mutableListOf<CartProduct>()
    // @formatter:off
    val products = listOf(
        Product(-99, "Gan 13 M MagLev", Category(-99, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-98, "MoYu RS3 M 2020", Category(-99, "3x3x3"), 899, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-97, "YuXin Little Magic", Category(-99, "3x3x3"), 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-96, "DaYan TengYun M", Category(-99, "3x3x3"), 2299, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-95, "YJ MGC 4", Category(-98, "4x4x4"), 1999, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-94, "MoYu AoSu WR M", Category(-98, "4x4x4"), 4399, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-93, "YJ MGC 5", Category(-97, "5x5x5"), 2299, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-92, "YuXin Cloud", Category(-97, "5x5x5"), 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
        Product(-91, "MoYu AoChuang GTS M", Category(-97, "5x5x5"), 3999, 0, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
    )
    // @formatter:on

    fun clear() {
        cart.clear()
    }

    fun getCart(): List<CartProduct> {
        return cart
    }

    fun addToCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false
        var result = true
        val cartProduct = cart.find { it.product!!.productId == product.productId }
        if (cartProduct != null && cartProduct.quantity + quantity <= product.inStock) {
            cartProduct.quantity += quantity
        } else if (cartProduct == null && quantity <= product.inStock) {
            val globalProduct = products.find { it.productId == product.productId }
            cart.add(CartProduct(globalProduct!!, quantity))
        } else result = false
        return result
    }

    internal fun removeFromCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false
        var result = false
        val existingProduct = cart.find { it.product!!.productId == product.productId }
        if (existingProduct != null && existingProduct.quantity - quantity >= 0) {
            existingProduct.quantity -= quantity
            if (existingProduct.quantity == 0)
                cart.remove(existingProduct)
            result = true
        }
        return result
    }
}
