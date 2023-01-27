package com.example.services

import com.example.DatabaseFactory.dbQuery
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

private fun resultRowToOrder(row: ResultRow) = Order(
    orderId = row[Orders.orderId],
    userId = row[Orders.userId],
    date = row[Orders.date],
    price = row[Orders.price],
    paid = row[Orders.paid],
    payment = row[Orders.payment]
)

private suspend fun readOrders(userId: Int): List<Order> = dbQuery {
    Orders.select { Orders.userId eq userId }.map { resultRowToOrder(it) }
}

private fun resultRowToOrderDetails(row: ResultRow) = OrderDetail(
    orderId = row[OrderDetails.orderId],
    productId = row[OrderDetails.productId],
    quantity = row[OrderDetails.quantity],
    price = row[OrderDetails.price]
)

private suspend fun readOrderDetails(userId: Int): List<OrderDetail> = dbQuery {
    (OrderDetails innerJoin Orders).select { Orders.userId eq userId }.map { resultRowToOrderDetails(it) }
}

fun Application.orderRoutes() {
    routing {
        authenticate("auth-user") {
            get("/me/orders") {
                val token = call.authentication.principal<JWTPrincipal>()
                val userId = token?.payload?.getClaim("userId")?.asInt()
                call.respond(readOrders(userId!!))
            }

            get("/me/order-details") {
                val token = call.authentication.principal<JWTPrincipal>()
                val userId = token?.payload?.getClaim("userId")?.asInt()
                call.respond(readOrderDetails(userId!!))
            }
        }
    }
}
