package com.example.app

class Product(
    val name: String,
    val category: String,
    val price: Double,
    val maxQuantity: Int,
    val description: String
)

class CartProduct(val product: Product, var quantity: Int)

object Cart {
    private val cartProducts = mutableListOf<CartProduct>()

    fun add(product: Product, quantity: Int): Boolean {
        var existingProduct: CartProduct? = null

        for (cartProduct in cartProducts) {
            if (cartProduct.product.name == product.name && cartProduct.product.category == product.category) {
                existingProduct = cartProduct
                break
            }
        }

        if (existingProduct != null && existingProduct.quantity + quantity <= existingProduct.product.maxQuantity)
            existingProduct.quantity += quantity
        else if (existingProduct == null && quantity <= product.maxQuantity)
            cartProducts.add(CartProduct(product, quantity))
        else return false
        return true
    }

    fun remove(product: Product): Boolean {
        var existingProduct: CartProduct? = null

        for (cartProduct in cartProducts) {
            if (cartProduct.product.name == product.name && cartProduct.product.category == product.category) {
                existingProduct = cartProduct
                break
            }
        }

        if (existingProduct != null && existingProduct.quantity > 1)
            existingProduct.quantity -= 1
        else if (existingProduct != null && existingProduct.quantity == 1)
            cartProducts.remove(existingProduct)
        else return false
        return true
    }

    fun get(): List<CartProduct> {
        return cartProducts
    }
}
