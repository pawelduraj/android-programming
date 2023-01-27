package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Order(
    var orderId: Int = -1,
    var userId: Int,
    var date: String,
    var price: Int,
    var paid: Boolean,
    var payment: String
)

object Orders : Table() {
    val orderId = integer("order_id").autoIncrement()
    val userId = integer("user_id").references(Users.userId)
    val date = varchar("date", 19)
    val price = integer("price")
    val paid = bool("paid")
    val payment = varchar("payment", 255)

    override val primaryKey = PrimaryKey(orderId)
}
