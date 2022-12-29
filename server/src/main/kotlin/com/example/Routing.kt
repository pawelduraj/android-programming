package com.example

import com.example.controllers.*
import com.example.services.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    productRoutes()
    categoryRoutes()
    userRoutes()
    authRoutes()
}
