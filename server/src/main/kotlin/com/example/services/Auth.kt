package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.DatabaseFactory.dbQuery
import com.example.models.*
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

private fun resultRowToUser(row: ResultRow) = User(
    userId = row[Users.userId],
    name = row[Users.name],
    email = row[Users.email],
    password = row[Users.password],
    admin = row[Users.admin]
)

@Serializable
private class RegisterBody(val name: String, val email: String, val password: String)

@Serializable
private class LoginBody(val email: String, val password: String)

private suspend fun createNewUser(body: RegisterBody): Boolean = dbQuery {
    Users.insert {
        it[name] = body.name
        it[email] = body.email
        it[password] = body.password
        it[admin] = false
    } get Users.userId > 0
}

private suspend fun getUser(body: LoginBody): User? = dbQuery {
    Users.select { Users.email eq body.email }.mapNotNull { resultRowToUser(it) }.singleOrNull()
}

fun Application.authRoutes() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val secret = config.propertyOrNull("jwt.secret")?.getString() ?: "keyboard cat"

    routing {
        post("/auth/register") {
            val body = call.receive<RegisterBody>()
            val name = body.name
            val email = body.email
            val pass = body.password
            if (name.trim().length != name.length || name.trim().isEmpty() || name.trim().length > 24)
                call.respondText("Name is invalid", status = io.ktor.http.HttpStatusCode.BadRequest)
            else if (email.trim().length != email.length || email.trim().isEmpty() || email.trim().length > 255)
                call.respondText("Email is invalid", status = io.ktor.http.HttpStatusCode.BadRequest)
            else if (pass.trim().length != pass.length || pass.trim().isEmpty() || pass.trim().length > 255)
                call.respondText("Password is invalid", status = io.ktor.http.HttpStatusCode.BadRequest)
            else if (getUser(LoginBody(email, pass)) != null)
                call.respondText("User already exists", status = io.ktor.http.HttpStatusCode.BadRequest)
            else {
                if (createNewUser(body))
                    call.respondText("User created", status = io.ktor.http.HttpStatusCode.Created)
                else
                    call.respondText("Error creating user", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }

        post("/auth/login") {
            val body = call.receive<LoginBody>()
            val email = body.email
            val pass = body.password
            if (email.trim().length != email.length || email.trim().isEmpty() || email.trim().length > 255)
                call.respondText("Email is invalid", status = io.ktor.http.HttpStatusCode.BadRequest)
            else if (pass.trim().length != pass.length || pass.trim().isEmpty() || pass.trim().length > 255)
                call.respondText("Password is invalid", status = io.ktor.http.HttpStatusCode.BadRequest)
            else {
                val user = getUser(body)
                if (user == null)
                    call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
                else if (user.password != pass)
                    call.respondText("Password is incorrect", status = io.ktor.http.HttpStatusCode.BadRequest)
                else {
                    val token = JWT.create()
                        .withClaim("userId", user.userId)
                        .withClaim("name", user.name)
                        .withClaim("email", user.email)
                        .withClaim("admin", user.admin)
                        .withExpiresAt(java.util.Date(System.currentTimeMillis() + 1000 * 60))
                        .sign(Algorithm.HMAC256(secret))
                    call.respondText(token, status = io.ktor.http.HttpStatusCode.OK)
                }
            }
        }
    }
}
