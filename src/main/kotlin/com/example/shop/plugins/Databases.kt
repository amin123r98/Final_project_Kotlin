package com.example.shop.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

fun Application.configureDatabases() {
    val logger = LoggerFactory.getLogger("Databases")

    // Пытаемся достать данные из конфига, если их нет - берем дефолтные
    val jdbcUrl = environment.config.propertyOrNull("db.jdbcUrl")?.getString() ?: "jdbc:postgresql://localhost:5432/shop_db"
    val user = environment.config.propertyOrNull("db.user")?.getString() ?: "shop_user"
    val password = environment.config.propertyOrNull("db.password")?.getString() ?: "shop_password"

    logger.info("Connecting to DB at: $jdbcUrl")
    logger.info("Using user: $user")

    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        setJdbcUrl(jdbcUrl)
        username = user
        setPassword(password)
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    try {
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .load()
            .migrate()

        logger.info("Database connection and migration successful!")
    } catch (e: Exception) {
        logger.error("Failed to connect to the database: ${e.message}")
        throw e
    }
}