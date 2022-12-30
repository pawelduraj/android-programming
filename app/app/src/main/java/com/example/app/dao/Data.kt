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

            val catCode = categoriesConnection.responseCode
            val prodCode = productsConnection.responseCode

            Database.realm.writeBlocking {
                val oldCartProducts: RealmResults<CartProduct> = this.query<CartProduct>().find()
                this.delete(oldCartProducts)

                if ((catCode == 200 && prodCode == 200) || catCode == 401 || prodCode == 401) {
                    val oldProducts: RealmResults<Product> = this.query<Product>().find()
                    this.delete(oldProducts)

                    val oldCategories: RealmResults<Category> = this.query<Category>().find()
                    this.delete(oldCategories)

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
}