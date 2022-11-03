package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Product(var productId : Int = -1, val name : String, val price : Int, val categoryId: Int)

object Products : Table() {
    val productId = integer("product_id").autoIncrement()
    val name = varchar("name", 64)
    val price = integer("price")
    val categoryId = integer("category_id").references(Categories.categoryId)

    override val primaryKey = PrimaryKey(productId)
}
