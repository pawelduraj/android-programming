package com.example

import io.ktor.server.application.*
import com.example.controllers.*

fun Application.configureRouting() {
    productRoutes()
    categoryRoutes()
}
