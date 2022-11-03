package com.example.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import com.example.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import com.example.models.*

private fun resultRowToProduct(row: ResultRow) = Product(
    productId = row[Products.productId],
    name = row[Products.name],
    price = row[Products.price],
    categoryId = row[Products.categoryId]
)

private suspend fun createProduct(product: Product): Boolean = dbQuery {
    Products.insert {
        it[name] = product.name
        it[price] = product.price
        it[categoryId] = product.categoryId
    } get Products.productId > 0
}

private suspend fun readProducts(): List<Product> = dbQuery {
    Products.selectAll().map { resultRowToProduct(it) }
}

private suspend fun readProduct(productId: Int): Product? = dbQuery {
    Products.select { Products.productId eq productId }.mapNotNull { resultRowToProduct(it) }.singleOrNull()
}

private suspend fun updateProduct(product: Product): Boolean = dbQuery {
    Products.update({ Products.productId eq product.productId }) {
        it[name] = product.name
        it[price] = product.price
        it[categoryId] = product.categoryId
    } > 0
}

private suspend fun deleteProduct(productId: Int): Boolean = dbQuery {
    Products.deleteWhere { Products.productId eq productId } > 0
}

fun Application.productRoutes() {
    routing {
        post("/products") {
            val product = call.receive<Product>()
            if (createProduct(product)) {
                call.respondText("Product created")
            } else {
                call.respondText("Error creating product")
            }
        }

        get("/products") {
            call.respond(readProducts())
        }

        get("/products/{id}") {
            val productId = call.parameters["id"]?.toIntOrNull()
            if (productId != null) {
                val product = readProduct(productId)
                if (product != null) {
                    call.respond(product)
                } else {
                    call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid category id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }

        put("/products/{id}") {
            val productId = call.parameters["id"]?.toIntOrNull()
            if (productId != null) {
                val product = call.receive<Product>()
                product.productId = productId
                val updatedProduct = updateProduct(product)
                if (updatedProduct) {
                    call.respondText("Product updated", status = io.ktor.http.HttpStatusCode.OK)
                } else {
                    call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid product id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }

        delete("/products/{id}") {
            val productId = call.parameters["id"]?.toIntOrNull()
            if (productId != null) {
                if (deleteProduct(productId)) {
                    call.respondText("Product deleted", status = io.ktor.http.HttpStatusCode.Accepted)
                } else {
                    call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid product id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }
}
