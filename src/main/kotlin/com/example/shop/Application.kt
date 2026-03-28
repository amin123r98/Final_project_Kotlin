package com.example.shop

import com.example.shop.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureSecurity()
//    configureSwagger() // Теперь это должно быть обычного цвета
    configureRouting()
}