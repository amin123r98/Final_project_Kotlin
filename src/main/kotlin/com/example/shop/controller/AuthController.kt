package com.example.shop.controller

import com.example.shop.domain.LoginRequest
import com.example.shop.domain.RegisterRequest
import com.example.shop.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(userService: UserService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val user = userService.register(request)

            if (user != null) {
                call.respond(HttpStatusCode.Created, user)
            } else {
                call.respond(HttpStatusCode.Conflict, "User already exists")
            }
        }
    }


    post("/login") {
        val request = call.receive<LoginRequest>()

        // Берем настройки JWT из конфига
        val secret = call.application.environment.config.property("jwt.secret").getString()
        val issuer = call.application.environment.config.property("jwt.issuer").getString()
        val audience = call.application.environment.config.property("jwt.audience").getString()

        val tokenResponse = userService.login(request, secret, issuer, audience)

        if (tokenResponse != null) {
            call.respond(tokenResponse)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
        }
    }

}