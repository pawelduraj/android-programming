package com.example.app.dao

import com.example.app.models.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

object Database {
    private val config = RealmConfiguration.Builder(
        setOf(Product::class, Category::class, CartProduct::class, User::class)
    ).build()
    private val realm = Realm.open(config)
    private val products: MutableList<Product> = mutableListOf()
    private val cartProducts: MutableList<CartProduct> = mutableListOf()

    init {
        Data.initDataIfEmpty(realm)
        sync()
    }

    fun getProducts(): List<Product> {
        return products
    }

    fun getCartProducts(): List<CartProduct> {
        return cartProducts
    }

    fun addToCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false

        var result = true
        realm.writeBlocking {
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
        if (result) sync()
        return result
    }

    fun removeFromCart(product: Product, quantity: Int): Boolean {
        if (quantity <= 0)
            return false

        var result = false
        realm.writeBlocking {
            val existingProduct: CartProduct? =
                this.query<CartProduct>("productId == $0", product.productId).first().find()
            if (existingProduct != null && existingProduct.quantity - quantity >= 0) {
                existingProduct.quantity -= quantity
                if (existingProduct.quantity == 0)
                    this.delete(existingProduct)
                result = true
            }
        }
        if (result) sync()
        return result
    }

    private fun sync() {
        products.clear()
        realm.query<Product>().find().forEach { products.add(it) }
        cartProducts.clear()
        realm.query<CartProduct>().find().forEach { cartProducts.add(it) }
    }
}
