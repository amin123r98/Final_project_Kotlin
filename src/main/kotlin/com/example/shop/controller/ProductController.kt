package com.example.shop.controller

import com.example.shop.domain.*
import com.example.shop.service.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(productService: ProductService) {
    route("/products") {
        get {
            call.respond(productService.getAllProducts())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val product = productService.getProductById(id)
            if (product != null) call.respond(product) else call.respond(HttpStatusCode.NotFound)
        }

        authenticate("auth-jwt") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can add products")
                    return@post
                }

                val request = call.receive<ProductRequest>()
                val newProduct = productService.createProduct(request)
                call.respond(HttpStatusCode.Created, newProduct)
            }
        }
    }
}