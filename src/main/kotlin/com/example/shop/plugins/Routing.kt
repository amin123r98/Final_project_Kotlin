package com.example.shop.plugins

import com.example.shop.controller.*
import com.example.shop.service.*
import com.example.shop.infrastructure.RabbitMQManager
import com.example.shop.worker.startOrderWorker
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import redis.clients.jedis.JedisPool

fun Application.configureRouting() {
    install(ContentNegotiation) { json() }

    // Настройки хостов (бери из yaml или дефолт)
    val redisHost = environment.config.propertyOrNull("redis.host")?.getString() ?: "localhost"
    val rabbitHost = environment.config.propertyOrNull("rabbitmq.host")?.getString() ?: "localhost"

    val redisPool = JedisPool(redisHost, 6379)
    val rabbitManager = RabbitMQManager(rabbitHost)

    // Запуск воркера
    startOrderWorker(rabbitHost)

    val userService = UserService()
    val productService = ProductService(redisPool)
    val orderService = OrderService(redisPool, rabbitManager)

    routing {
        authRoutes(userService)
        productRoutes(productService)
        orderRoutes(orderService)

    }
}