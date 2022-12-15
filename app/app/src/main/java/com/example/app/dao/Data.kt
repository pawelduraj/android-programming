package com.example.app.dao

import android.os.StrictMode
import com.example.app.models.CartProduct
import com.example.app.models.Category
import com.example.app.models.Product
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

const val API_URL = "http://192.168.0.129:5000" // TODO move to env

class KtorCategory {
    val categoryId: Int = -1
    val name: String = ""
}

class KtorProduct {
    val productId: Int = -1
    val name: String = ""
    val categoryId: Int = -1
    val price: Int = -1
    val inStock: Int = -1
    val description: String = ""
}

object Data {
    fun initDataIfEmpty(realm: Realm) {
        val mapper = jacksonObjectMapper()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var ktorCategories = mutableListOf<KtorCategory>()
        var ktorProducts = mutableListOf<KtorProduct>()

        try {
            val categoriesURL = URL("$API_URL/categories")
            val categoriesConnection = categoriesURL.openConnection() as HttpURLConnection
            categoriesConnection.requestMethod = "GET"
            categoriesConnection.setRequestProperty("Content-Type", "application/json")
            categoriesConnection.setRequestProperty("Accept", "application/json")
            BufferedReader(
                InputStreamReader(categoriesConnection.inputStream, "utf-8")
            ).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim { it <= ' ' })
                }
                ktorCategories = mapper.readValue(
                    response.toString(),
                    mapper.typeFactory.constructCollectionType(
                        List::class.java,
                        KtorCategory::class.java
                    )
                )
            }

            val productsURL = URL("$API_URL/products")
            val productsConnection = productsURL.openConnection() as HttpURLConnection
            productsConnection.requestMethod = "GET"
            productsConnection.setRequestProperty("Content-Type", "application/json")
            productsConnection.setRequestProperty("Accept", "application/json")
            BufferedReader(
                InputStreamReader(productsConnection.inputStream, "utf-8")
            ).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim { it <= ' ' })
                }
                ktorProducts = mapper.readValue(
                    response.toString(),
                    mapper.typeFactory.constructCollectionType(
                        List::class.java,
                        KtorProduct::class.java
                    )
                )
            }

            realm.writeBlocking {
                // Remove old data

                val categoriesToDelete: RealmResults<Category> = this.query<Category>().find()
                this.delete(categoriesToDelete)

                val productsToDelete: RealmResults<Product> = this.query<Product>().find()
                this.delete(productsToDelete)

                val cartProductsToDelete: RealmResults<CartProduct> =
                    this.query<CartProduct>().find()
                this.delete(cartProductsToDelete)

                // Add data from API to Realm

                val categories = mutableListOf<Category>()
                ktorCategories.forEach() {
                    categories.add(Category(it.categoryId, it.name))
                }

                val copiedCategories: MutableList<Category> = mutableListOf()
                categories.forEach { category -> copiedCategories.add(this.copyToRealm(category)) }

                val products = mutableListOf<Product>()
                ktorProducts.forEach() {
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
        } catch (e: Exception) {
            println("Cannot load products: $e")
        }
    }
}
