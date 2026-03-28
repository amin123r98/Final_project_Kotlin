package com.example.shop.repository

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50).default("USER")
}

object Products : IntIdTable("products") {
    val name = varchar("name", 255)
    val price = decimal("price", 10, 2)
    val stock = integer("stock")
}

object Orders : IntIdTable("orders") {
    val userId = reference("user_id", Users)
    val totalPrice = decimal("total_price", 10, 2)
    val status = varchar("status", 50).default("PENDING")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

object OrderItems : IntIdTable("order_items") {
    val orderId = reference("order_id", Orders)
    val productId = reference("product_id", Products)
    val quantity = integer("quantity")
    val price = decimal("price", 10, 2)
}

object AuditLogs : IntIdTable("audit_logs") {
    val action = varchar("action", 255)
    val details = text("details")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}