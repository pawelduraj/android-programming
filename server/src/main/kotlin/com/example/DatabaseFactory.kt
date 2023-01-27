package com.example

import com.example.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    fun init() {
        val jdbcURL = "jdbc:h2:file:./sample"
        val driverClassName = "org.h2.Driver"
        val database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            SchemaUtils.create(Categories)
            SchemaUtils.create(Products)
            SchemaUtils.create(Users)
            SchemaUtils.create(Orders)
            SchemaUtils.create(OrderDetails)

            if (Categories.selectAll().count() == 0.toLong()) {
                for (category in SampleData.getCategories()) Categories.insert {
                    it[categoryId] = category.categoryId
                    it[name] = category.name
                }

                for (product in SampleData.getProducts()) Products.insert {
                    it[productId] = product.productId
                    it[name] = product.name
                    it[categoryId] = product.categoryId
                    it[price] = product.price
                    it[inStock] = product.inStock
                    it[description] = product.description
                }

                for (user in SampleData.getUsers()) Users.insert {
                    it[userId] = user.userId
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = user.password
                    it[admin] = user.admin
                }

                for (order in SampleData.getOrders()) Orders.insert {
                    it[orderId] = order.orderId
                    it[userId] = order.userId
                    it[date] = order.date
                    it[price] = order.price
                    it[paid] = order.paid
                    it[payment] = order.payment
                }

                for (orderDetail in SampleData.getOrderDetails()) OrderDetails.insert {
                    it[orderId] = orderDetail.orderId
                    it[productId] = orderDetail.productId
                    it[quantity] = orderDetail.quantity
                    it[price] = orderDetail.price
                }
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
