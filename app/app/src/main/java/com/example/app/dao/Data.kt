package com.example.app.dao

import android.util.Log
import com.example.app.models.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal object Data {
    internal fun init() {
        val mapper = jacksonObjectMapper()
        val jwt = Database.getUser()?.jwt ?: ""
        var ktorCategories = mutableListOf<KtorCategory>()
        var ktorProducts = mutableListOf<KtorProduct>()
        var ktorOrders = mutableListOf<KtorOrder>()
        var ktorOrderDetails = mutableListOf<KtorOrderDetail>()

        try {
            val categoriesURL = URL("${Config.API_URL}/categories")
            val categoriesConnection = categoriesURL.openConnection() as HttpURLConnection
            categoriesConnection.requestMethod = "GET"
            categoriesConnection.connectTimeout = 3000
            categoriesConnection.setRequestProperty("Content-Type", "application/json")
            categoriesConnection.setRequestProperty("Accept", "application/json")
            categoriesConnection.setRequestProperty("Authorization", "Bearer $jwt")
            categoriesConnection.connect()

            if (categoriesConnection.responseCode == 200) {
                BufferedReader(
                    InputStreamReader(categoriesConnection.inputStream, "utf-8")
                ).use { br ->
                    val response = StringBuilder()
                    var responseLine: String?
                    while (br.readLine().also { responseLine = it } != null) response.append(
                        responseLine!!.trim { it <= ' ' })
                    ktorCategories = mapper.readValue(
                        response.toString(), mapper.typeFactory.constructCollectionType(
                            List::class.java, KtorCategory::class.java
                        )
                    )
                }
            }

            val productsURL = URL("${Config.API_URL}/products")
            val productsConnection = productsURL.openConnection() as HttpURLConnection
            productsConnection.requestMethod = "GET"
            productsConnection.connectTimeout = 3000
            productsConnection.setRequestProperty("Content-Type", "application/json")
            productsConnection.setRequestProperty("Accept", "application/json")
            productsConnection.setRequestProperty("Authorization", "Bearer $jwt")
            productsConnection.connect()

            Log.e("Response code", productsConnection.responseCode.toString())

            if (productsConnection.responseCode == 200) {
                BufferedReader(
                    InputStreamReader(productsConnection.inputStream, "utf-8")
                ).use { br ->
                    val response = StringBuilder()
                    var responseLine: String?
                    while (br.readLine().also { responseLine = it } != null) response.append(
                        responseLine!!.trim { it <= ' ' })
                    ktorProducts = mapper.readValue(
                        response.toString(), mapper.typeFactory.constructCollectionType(
                            List::class.java, KtorProduct::class.java
                        )
                    )
                }
            }

            val ordersURL = URL("${Config.API_URL}/me/orders")
            val ordersConnection = ordersURL.openConnection() as HttpURLConnection
            ordersConnection.requestMethod = "GET"
            ordersConnection.connectTimeout = 3000
            ordersConnection.setRequestProperty("Content-Type", "application/json")
            ordersConnection.setRequestProperty("Accept", "application/json")
            ordersConnection.setRequestProperty("Authorization", "Bearer $jwt")
            ordersConnection.connect()

            if (ordersConnection.responseCode == 200) {
                BufferedReader(
                    InputStreamReader(ordersConnection.inputStream, "utf-8")
                ).use { br ->
                    val response = StringBuilder()
                    var responseLine: String?
                    while (br.readLine().also { responseLine = it } != null) response.append(
                        responseLine!!.trim { it <= ' ' })
                    ktorOrders = mapper.readValue(
                        response.toString(), mapper.typeFactory.constructCollectionType(
                            List::class.java, KtorOrder::class.java
                        )
                    )
                }
            }

            val orderDetailsURL = URL("${Config.API_URL}/me/order-details")
            val orderDetailsConnection = orderDetailsURL.openConnection() as HttpURLConnection
            orderDetailsConnection.requestMethod = "GET"
            orderDetailsConnection.connectTimeout = 3000
            orderDetailsConnection.setRequestProperty("Content-Type", "application/json")
            orderDetailsConnection.setRequestProperty("Accept", "application/json")
            orderDetailsConnection.setRequestProperty("Authorization", "Bearer $jwt")
            orderDetailsConnection.connect()

            if (orderDetailsConnection.responseCode == 200) {
                BufferedReader(
                    InputStreamReader(orderDetailsConnection.inputStream, "utf-8")
                ).use { br ->
                    val response = StringBuilder()
                    var responseLine: String?
                    while (br.readLine().also { responseLine = it } != null) response.append(
                        responseLine!!.trim { it <= ' ' })
                    ktorOrderDetails = mapper.readValue(
                        response.toString(), mapper.typeFactory.constructCollectionType(
                            List::class.java, KtorOrderDetail::class.java
                        )
                    )
                }
            }

            val catCode = categoriesConnection.responseCode
            val prodCode = productsConnection.responseCode
            val ordCode = ordersConnection.responseCode
            val ordDetCode = orderDetailsConnection.responseCode

            Database.realm.writeBlocking {
                val oldCartProducts: RealmResults<CartProduct> = this.query<CartProduct>().find()
                this.delete(oldCartProducts)

                if ((catCode == 200 && prodCode == 200 && ordCode == 200 && ordDetCode == 200)
                    || catCode == 401 || prodCode == 401 || ordCode == 401 || ordDetCode == 401
                ) {
                    val oldProducts: RealmResults<Product> = this.query<Product>().find()
                    this.delete(oldProducts)

                    val oldCategories: RealmResults<Category> = this.query<Category>().find()
                    this.delete(oldCategories)

                    val oldOrders: RealmResults<Order> = this.query<Order>().find()
                    this.delete(oldOrders)

                    val oldOrderDetails: RealmResults<OrderDetail> =
                        this.query<OrderDetail>().find()
                    this.delete(oldOrderDetails)

                    val categories = mutableListOf<Category>()
                    ktorCategories.forEach { categories.add(Category(it.categoryId, it.name)) }

                    val copiedCategories: MutableList<Category> = mutableListOf()
                    categories.forEach { category -> copiedCategories.add(this.copyToRealm(category)) }

                    val products = mutableListOf<Product>()
                    ktorProducts.forEach {
                        products.add(
                            Product(
                                it.productId,
                                it.name,
                                copiedCategories.find { category -> category.categoryId == it.categoryId }!!,
                                it.price,
                                it.inStock,
                                it.description
                            )
                        )
                    }

                    products.forEach { product -> this.copyToRealm(product) }

                    ktorOrders.forEach { order ->
                        this.copyToRealm(
                            Order(
                                order.orderId,
                                order.userId,
                                order.date,
                                order.price,
                                order.paid,
                                order.payment
                            )
                        )
                    }

                    ktorOrderDetails.forEach { orderDetail ->
                        this.copyToRealm(
                            OrderDetail(
                                orderDetail.orderId,
                                orderDetail.productId,
                                orderDetail.quantity,
                                orderDetail.price
                            )
                        )
                    }
                }

                if (catCode == 401 || prodCode == 401) {
                    val oldUsers: RealmResults<User> = this.query<User>().find()
                    this.delete(oldUsers)
                }
            }

            Database.sync()
        } catch (e: Exception) {
            Log.e("DATA", e.toString())
            e.printStackTrace()
        }
    }

    private class KtorCategory {
        val categoryId: Int = -1
        val name: String = ""
    }

    private class KtorProduct {
        val productId: Int = -1
        val name: String = ""
        val categoryId: Int = -1
        val price: Int = -1
        val inStock: Int = -1
        val description: String = ""
    }

    private class KtorOrder {
        val orderId: Int = -1
        val userId: Int = -1
        val date: String = ""
        val price: Int = -1
        val paid: Boolean = false
        val payment: String = ""
    }

    private class KtorOrderDetail {
        val orderId: Int = -1
        val productId: Int = -1
        val quantity: Int = -1
        val price: Int = -1
    }
}
