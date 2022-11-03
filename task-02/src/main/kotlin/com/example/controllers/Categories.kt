package com.example.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import com.example.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import com.example.models.*

private fun resultRowToCategory(row: ResultRow) = Category(
    categoryId = row[Categories.categoryId],
    name = row[Categories.name],
    description = row[Categories.description],
    keywords = row[Categories.keywords]
)

private suspend fun createCategory(category: Category): Boolean = dbQuery {
    Categories.insert {
        it[name] = category.name
        it[description] = category.description
        it[keywords] = category.keywords
    } get Categories.categoryId > 0
}

private suspend fun readCategories(): List<Category> = dbQuery {
    Categories.selectAll().map { resultRowToCategory(it) }
}

private suspend fun readCategory(categoryId: Int): Category? = dbQuery {
    Categories.select { Categories.categoryId eq categoryId }.mapNotNull { resultRowToCategory(it) }.singleOrNull()
}

private suspend fun updateCategory(category: Category): Boolean = dbQuery {
    Categories.update({ Categories.categoryId eq category.categoryId }) {
        it[name] = category.name
        it[description] = category.description
        it[keywords] = category.keywords
    } > 0
}

private suspend fun deleteCategory(categoryId: Int): Boolean = dbQuery {
    Categories.deleteWhere { Categories.categoryId eq categoryId } > 0
}

fun Application.categoryRoutes() {
    routing {
        post("/categories") {
            val category = call.receive<Category>()
            if (createCategory(category)) {
                call.respondText("Category created")
            } else {
                call.respondText("Error creating category")
            }
        }

        get("/categories") {
            call.respond(readCategories())
        }

        get("/categories/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            if (categoryId != null) {
                val category = readCategory(categoryId)
                if (category != null) {
                    call.respond(category)
                } else {
                    call.respondText("Category not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid category id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }

        put("/categories/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            if (categoryId != null) {
                val category = call.receive<Category>()
                category.categoryId = categoryId
                val updatedCategory = updateCategory(category)
                if (updatedCategory) {
                    call.respondText("Category updated", status = io.ktor.http.HttpStatusCode.OK)
                } else {
                    call.respondText("Category not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid category id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }

        delete("/categories/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            if (categoryId != null) {
                if (deleteCategory(categoryId)) {
                    call.respondText("Category deleted", status = io.ktor.http.HttpStatusCode.Accepted)
                } else {
                    call.respondText("Category not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } else {
                call.respondText("Invalid category id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }
}
