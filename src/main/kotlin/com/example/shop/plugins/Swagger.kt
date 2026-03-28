package com.example.shop.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        info {
            title = "Ktor Shop API"
            version = "1.0.0"
            description = "Backend service for online shop"
        }
        // Мы убрали блоки swagger { ... }, чтобы не было ошибок "Unresolved reference"
    }
}