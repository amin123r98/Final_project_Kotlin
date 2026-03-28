package com.example.shop.controller

import com.example.shop.domain.OrderRequest
import com.example.shop.service.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRoutes(orderService: OrderService) {
    authenticate("auth-jwt") {
        post("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<OrderRequest>()
            val order = orderService.createOrder(userId, request)
            call.respond(HttpStatusCode.Created, order)
        }

        get("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            call.respond(orderService.getOrderHistory(userId))
        }

        get("/stats/orders") {
            val principal = call.principal<JWTPrincipal>()
            if (principal?.payload?.getClaim("role")?.asString() != "ADMIN") {
                return@get call.respond(HttpStatusCode.Forbidden, "Admins only")
            }
            call.respond(orderService.getOrderStats())
        }




    }
}


