package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Product(
    var productId: Int = -1,
    val name: String,
    val categoryId: Int,
    val price: Int,
    val inStock: Int,
    val description: String
)

object Products : Table() {
    val productId = integer("product_id").autoIncrement()
    val name = varchar("name", 64)
    val categoryId = integer("category_id").references(Categories.categoryId)
    val price = integer("price")
    var inStock = integer("in_stock")
    var description = varchar("description", 2048)

    override val primaryKey = PrimaryKey(productId)
}
