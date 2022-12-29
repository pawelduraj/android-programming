package com.example.controllers

import com.example.DatabaseFactory.dbQuery
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

private fun resultRowToProduct(row: ResultRow) = Product(
    productId = row[Products.productId],
    name = row[Products.name],
    categoryId = row[Products.categoryId],
    price = row[Products.price],
    inStock = row[Products.inStock],
    description = row[Products.description]
)

private suspend fun createProduct(product: Product): Boolean = dbQuery {
    Products.insert {
        it[name] = product.name
        it[categoryId] = product.categoryId
        it[price] = product.price
        it[inStock] = product.inStock
        it[description] = product.description
    } get Products.productId > 0
}

private suspend fun readUsers(): List<Product> = dbQuery {
    Products.selectAll().map { resultRowToProduct(it) }
}

private suspend fun readUser(productId: Int): Product? = dbQuery {
    Products.select { Products.productId eq productId }.mapNotNull { resultRowToProduct(it) }.singleOrNull()
}

private suspend fun updateUser(product: Product): Boolean = dbQuery {
    Products.update({ Products.productId eq product.productId }) {
        it[name] = product.name
        it[categoryId] = product.categoryId
        it[price] = product.price
        it[inStock] = product.inStock
        it[description] = product.description
    } > 0
}

private suspend fun deleteUser(productId: Int): Boolean = dbQuery {
    Products.deleteWhere { Products.productId eq productId } > 0
}

fun Application.productRoutes() {
    routing {
        authenticate("auth-user") {
            get("/products") {
                call.respond(readUsers())
            }

            get("/products/{id}") {
                val productId = call.parameters["id"]?.toIntOrNull()
                if (productId != null) {
                    val product = readUser(productId)
                    if (product != null)
                        call.respond(product)
                    else
                        call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid product id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-admin") {
            post("/products") {
                val product = call.receive<Product>()
                if (createProduct(product))
                    call.respondText("Product created")
                else
                    call.respondText("Error creating product")
            }

            put("/products/{id}") {
                val productId = call.parameters["id"]?.toIntOrNull()
                if (productId != null) {
                    val product = call.receive<Product>()
                    product.productId = productId
                    val updatedProduct = updateUser(product)
                    if (updatedProduct)
                        call.respondText("Product updated", status = io.ktor.http.HttpStatusCode.OK)
                    else
                        call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid product id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }

            delete("/products/{id}") {
                val productId = call.parameters["id"]?.toIntOrNull()
                if (productId != null) {
                    if (deleteUser(productId))
                        call.respondText("Product deleted", status = io.ktor.http.HttpStatusCode.Accepted)
                    else
                        call.respondText("Product not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid product id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }
}
