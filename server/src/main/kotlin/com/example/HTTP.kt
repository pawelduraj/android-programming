package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.*

fun Application.configureHTTP() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val secret = config.propertyOrNull("jwt.secret")?.getString() ?: "keyboard cat"

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        anyHost()
    }

    authentication {
        jwt("auth-user") {
            verifier(JWT.require(Algorithm.HMAC256(secret)).build())

            validate { credential ->
                JWTPrincipal(credential.payload)
            }
        }

        jwt("auth-admin") {
            verifier(JWT.require(Algorithm.HMAC256(secret)).build())

            validate { credential ->
                if (credential.payload.getClaim("admin").asBoolean() == true)
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}
