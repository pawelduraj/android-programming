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

private fun resultRowToUser(row: ResultRow) = User(
    userId = row[Users.userId],
    name = row[Users.name],
    email = row[Users.email],
    password = row[Users.password],
    admin = row[Users.admin]
)

private suspend fun createUser(user: User): Boolean = dbQuery {
    Users.insert {
        it[name] = user.name
        it[email] = user.email
        it[password] = user.password
        it[admin] = user.admin
    } get Users.userId > 0
}

private suspend fun readUsers(): List<User> = dbQuery {
    Users.selectAll().map { resultRowToUser(it) }
}

private suspend fun readUser(userId: Int): User? = dbQuery {
    Users.select { Users.userId eq userId }.mapNotNull { resultRowToUser(it) }.singleOrNull()
}

private suspend fun updateUser(user: User): Boolean = dbQuery {
    Users.update({ Users.userId eq user.userId }) {
        it[name] = user.name
        it[email] = user.email
        it[password] = user.password
        it[admin] = user.admin
    } > 0
}

private suspend fun deleteUser(userId: Int): Boolean = dbQuery {
    Users.deleteWhere { Users.userId eq userId } > 0
}

fun Application.userRoutes() {
    routing {
        authenticate("auth-admin") {
            get("/users") {
                call.respond(readUsers())
            }

            get("/users/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    val user = readUser(userId)
                    if (user != null)
                        call.respond(user)
                    else
                        call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid user id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }

            post("/users") {
                val user = call.receive<User>()
                if (createUser(user))
                    call.respondText("User created")
                else
                    call.respondText("Error creating user")
            }

            put("/users/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    val user = call.receive<User>()
                    user.userId = userId
                    val updatedUser = updateUser(user)
                    if (updatedUser)
                        call.respondText("User updated", status = io.ktor.http.HttpStatusCode.OK)
                    else
                        call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid user id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }

            delete("/users/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    if (deleteUser(userId))
                        call.respondText("User deleted", status = io.ktor.http.HttpStatusCode.Accepted)
                    else
                        call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else
                    call.respondText("Invalid user id", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }
}
