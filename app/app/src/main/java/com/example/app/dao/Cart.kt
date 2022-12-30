package com.example.app.dao

import com.example.app.models.CartProduct
import com.example.app.models.Product
import io.realm.kotlin.ext.query

internal object Cart {
    internal fun addToCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false

        var result = true
        Database.realm.writeBlocking {
            val realmCartProduct: CartProduct? =
                this.query<CartProduct>("productId == $0", product.productId).first().find()
            if (realmCartProduct != null && realmCartProduct.quantity + quantity <= product.inStock) {
                realmCartProduct.quantity += quantity
            } else if (realmCartProduct == null && quantity <= product.inStock) {
                val realmProduct: Product =
                    this.query<Product>("productId == $0", product.productId).first().find()!!
                val cartProduct = CartProduct(realmProduct, quantity)
                this.copyToRealm(cartProduct)
            } else result = false
        }
        if (result) Database.sync()
        return result
    }

    internal fun removeFromCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false

        var result = false
        Database.realm.writeBlocking {
            val existingProduct: CartProduct? =
                this.query<CartProduct>("productId == $0", product.productId).first().find()
            if (existingProduct != null && existingProduct.quantity - quantity >= 0) {
                existingProduct.quantity -= quantity
                if (existingProduct.quantity == 0)
                    this.delete(existingProduct)
                result = true
            }
        }
        if (result) Database.sync()
        return result
    }
}
