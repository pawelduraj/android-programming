package com.example.app.dao

import android.os.StrictMode
import com.example.app.models.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

object Database {
    private val config = RealmConfiguration.Builder(
        setOf(
            Product::class, Category::class, CartProduct::class,
            User::class, Order::class, OrderDetail::class
        )
    ).deleteRealmIfMigrationNeeded().build()
    internal val realm = Realm.open(config)
    private val categories: MutableList<Category> = mutableListOf()
    private val products: MutableList<Product> = mutableListOf()
    private val cartProducts: MutableList<CartProduct> = mutableListOf()
    private val users: MutableList<User> = mutableListOf()
    private val orders: MutableList<Order> = mutableListOf()
    private val orderDetails: MutableList<OrderDetail> = mutableListOf()

    init {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        sync()
        init()
    }

    fun getCategories(): List<Category> {
        return categories
    }

    fun getProducts(): List<Product> {
        return products
    }

    fun getCartProducts(): List<CartProduct> {
        return cartProducts
    }

    fun getUser(): User? {
        return users.firstOrNull()
    }

    fun getOrders(): List<Order> {
        return orders
    }

    fun getOrderDetails(): List<OrderDetail> {
        return orderDetails
    }

    // CART

    fun addToCart(product: Product, quantity: Int): Boolean {
        return Cart.addToCart(product, quantity)
    }

    fun removeFromCart(product: Product, quantity: Int): Boolean {
        return Cart.removeFromCart(product, quantity)
    }

    fun getProductsByOrderId(orderId: Int): List<Product> {
        return Cart.getProductsByOrderId(orderId)
    }

    // AUTH

    fun signUp(name: String, email: String, password: String): Boolean {
        return Auth.signUp(name, email, password)
    }

    fun signIn(email: String, password: String): Boolean {
        return Auth.signIn(email, password)
    }

    fun signOut() {
        Auth.signOut()
    }

    fun signInWithGithub(token: String): Boolean {
        return Auth.signInWithGithub(token)
    }

    fun signInWithGoogle(token: String): Boolean {
        return Auth.signInWithGoogle(token)
    }

    // PAYMENTS

    fun buy(): Boolean {
        return Payments.buy()
    }

    fun payStripe(orderId: Int): Boolean {
        return Payments.payStripe(orderId)
    }

    fun payPayU(orderId: Int): String {
        return Payments.payPayU(orderId)
    }

    fun finalizePayment(orderId: Int): Boolean {
        return Payments.finalizePayment(orderId)
    }

    // ADMIN

    fun addNewProduct(
        name: String, category: Category, price: Int, inStock: Int, description: String
    ): Boolean {
        return Admin.addNewProduct(name, category, price, inStock, description)
    }

    // SYNC

    internal fun init() {
        Data.init()
    }

    internal fun sync() {
        categories.clear()
        realm.query<Category>().find().forEach { categories.add(it) }
        products.clear()
        realm.query<Product>().find().forEach { products.add(it) }
        cartProducts.clear()
        realm.query<CartProduct>().find().forEach { cartProducts.add(it) }
        users.clear()
        realm.query<User>().find().forEach { users.add(it) }
        orders.clear()
        realm.query<Order>().find().forEach { orders.add(it) }
        orderDetails.clear()
        realm.query<OrderDetail>().find().forEach { orderDetails.add(it) }
    }
}
