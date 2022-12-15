package com.example.app.dao

import com.example.app.models.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Database {
    private val config = RealmConfiguration.Builder(
        setOf(Product::class, Category::class, CartProduct::class, User::class)
    ).build()
    private val realm = Realm.open(config)
    private val categories: MutableList<Category> = mutableListOf()
    private val products: MutableList<Product> = mutableListOf()
    private val cartProducts: MutableList<CartProduct> = mutableListOf()

    init {
        Data.initDataIfEmpty(realm)
        sync()
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
        categories.clear()
        realm.query<Category>().find().forEach { categories.add(it) }
        products.clear()
        realm.query<Product>().find().forEach { products.add(it) }
        cartProducts.clear()
        realm.query<CartProduct>().find().forEach { cartProducts.add(it) }
    }

    fun addNewProduct(
        name: String,
        category: Category,
        price: Int,
        inStock: Int,
        description: String
    ): Boolean {
        try {
            val url = URL("http://192.168.0.129:5000/products")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true
            val productJson =
                "{\"name\": \"$name\", \"categoryId\": \"${category.categoryId}\", \"price\": $price, \"inStock\": $inStock, \"description\": \"$description\"}"

            con.outputStream.use { os ->
                val input: ByteArray = productJson.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            BufferedReader(
                InputStreamReader(con.inputStream, "utf-8")
            ).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim { it <= ' ' })
                }
                println(response.toString())
            }

            Data.initDataIfEmpty(realm)
            return true
        } catch (e: Exception) {
            println(e)
            return false
        }
    }
}
