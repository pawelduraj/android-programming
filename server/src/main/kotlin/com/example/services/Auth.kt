package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.DatabaseFactory.dbQuery
import com.example.models.*
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
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

@Serializable
private class TokenBody(val token: String)

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

private fun createToken(userId: Int, name: String, email: String, admin: Boolean, secret: String, exp: Int): String {
    return JWT.create()
        .withClaim("userId", userId)
        .withClaim("name", name)
        .withClaim("email", email)
        .withClaim("admin", admin)
        .withExpiresAt(java.util.Date(System.currentTimeMillis() + exp * 60000))
        .sign(Algorithm.HMAC256(secret))
}

@Suppress("LocalVariableName", "DuplicatedCode")
fun Application.authRoutes() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val JWT_SECRET = config.propertyOrNull("jwt.secret")?.getString() ?: "keyboard cat"
    val JWT_EXP = config.propertyOrNull("jwt.exp")?.getString()?.toInt() ?: 60000
    val GITHUB_CLIENT_ID = config.propertyOrNull("github.client.id")?.getString() ?: "null"
    val GITHUB_CLIENT_SECRET = config.propertyOrNull("github.client.secret")?.getString() ?: "null"

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
            else if (createNewUser(body))
                call.respondText("User created", status = io.ktor.http.HttpStatusCode.Created)
            else
                call.respondText("Error creating user", status = io.ktor.http.HttpStatusCode.InternalServerError)

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
                    val token = createToken(user.userId, user.name, user.email, user.admin, JWT_SECRET, JWT_EXP)
                    call.respondText(token, status = io.ktor.http.HttpStatusCode.OK)
                }
            }
        }

        post("/auth/github") {
            val body = call.receive<TokenBody>()
            val token = body.token

            try {
                val client = HttpClient()
                val accessTokenResponse: HttpResponse = client.post("https://github.com/login/oauth/access_token") {
                    parameter("client_id", GITHUB_CLIENT_ID)
                    parameter("client_secret", GITHUB_CLIENT_SECRET)
                    parameter("code", token)
                }

                if (accessTokenResponse.status.value != 200)
                    throw Exception("Error getting access token")

                val accessToken = accessTokenResponse.body<String>().split("&")[0].split("=")[1]

                val userDataResponse: HttpResponse = client.get("https://api.github.com/user") {
                    header("Authorization", "Bearer $accessToken")
                }

                if (userDataResponse.status.value != 200)
                    throw Exception("Error getting user data")

                val userData = userDataResponse.body<String>()
                val name = Json.parseToJsonElement(userData).jsonObject["name"]?.jsonPrimitive?.content.toString()

                val userEmailResponse: HttpResponse = client.get("https://api.github.com/user/emails") {
                    header("Authorization", "Bearer $accessToken")
                }

                val userEmail = userEmailResponse.body<String>()
                val email =
                    Json.parseToJsonElement(userEmail).jsonArray[0].jsonObject["email"]?.jsonPrimitive?.content.toString()

                val user = getUser(LoginBody(email, ""))

                if (user == null && !createNewUser(RegisterBody(name, email, "")))
                    throw Exception("Error creating user")

                val u = getUser(LoginBody(email, ""))!!
                val newToken = createToken(u.userId, u.name, u.email, u.admin, JWT_SECRET, JWT_EXP)

                call.respondText(newToken, status = io.ktor.http.HttpStatusCode.OK)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Error", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }

        post("/auth/google") {
            val body = call.receive<TokenBody>()
            val token = body.token

            try {
                val client = HttpClient()
                val userDataResponse: HttpResponse =
                    client.get("https://oauth2.googleapis.com/tokeninfo?id_token=$token")

                val userData = userDataResponse.body<String>()
                val name = Json.parseToJsonElement(userData).jsonObject["name"]?.jsonPrimitive?.content.toString()
                val email = Json.parseToJsonElement(userData).jsonObject["email"]?.jsonPrimitive?.content.toString()

                if (userDataResponse.status.value != 200)
                    throw Exception("Error getting user data")

                val user = getUser(LoginBody(email, ""))

                if (user == null && !createNewUser(RegisterBody(name, email, "")))
                    throw Exception("Error creating user")

                val u = getUser(LoginBody(email, ""))!!
                val newToken = createToken(u.userId, u.name, u.email, u.admin, JWT_SECRET, JWT_EXP)

                call.respondText(newToken, status = io.ktor.http.HttpStatusCode.OK)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Error", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
    }
}
