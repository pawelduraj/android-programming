package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class OrderDetail(
    var orderId: Int,
    var productId: Int,
    var quantity: Int,
    var price: Int
)

object OrderDetails : Table() {
    val orderId = integer("order_id").references(Orders.orderId)
    val productId = integer("product_id").references(Products.productId)
    val quantity = integer("quantity")
    val price = integer("price")

    override val primaryKey = PrimaryKey(orderId, productId)
}
