package com.example.shop.service

import com.example.shop.domain.OrderRequest
import com.example.shop.domain.OrderResponse
import com.example.shop.infrastructure.RabbitMQManager
import com.example.shop.repository.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import redis.clients.jedis.JedisPool
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrderService(
    private val redisPool: JedisPool,
    private val rabbitMQ: RabbitMQManager
) {
    fun createOrder(userId: Int, request: OrderRequest): OrderResponse {
        return transaction {
            var total = 0.0

            // 1. Проверяем наличие товаров и считаем сумму
            request.items.forEach { item ->
                val product = Products.select { Products.id eq item.productId }.single()
                val currentStock = product[Products.stock]

                if (currentStock < item.quantity) throw Exception("Not enough stock for product ${item.productId}")

                total += product[Products.price].toDouble() * item.quantity

                // Уменьшаем stock
                Products.update({ Products.id eq item.productId }) {
                    it[stock] = currentStock - item.quantity
                }
            }

            // 2. Создаем заказ
            val orderId = Orders.insertAndGetId {
                it[Orders.userId] = userId
                it[totalPrice] = total.toBigDecimal()
            }

            // 3. Создаем записи о товарах в заказе
            request.items.forEach { item ->
                val product = Products.select { Products.id eq item.productId }.single()
                OrderItems.insert {
                    it[Orders.id] = orderId
                    it[productId] = item.productId
                    it[quantity] = item.quantity
                    it[price] = product[Products.price]
                }
            }

            // 4. Пишем в Audit Logs
            AuditLogs.insert {
                it[action] = "CREATE_ORDER"
                it[details] = "User $userId created order ${orderId.value} for total $total"
            }

            val response = OrderResponse(orderId.value, userId, total, "PENDING")

            // 5. Кэшируем в Redis и отправляем в RabbitMQ
            redisPool.resource.use { it.setex("order:${orderId.value}", 3600, Json.encodeToString(response)) }
            rabbitMQ.sendOrderMessage("New order created: ${orderId.value} by user $userId")

            response
        }
    }

    fun getOrderHistory(userId: Int): List<OrderResponse> {
        return transaction {
            Orders.select { Orders.userId eq userId }.map {
                OrderResponse(it[Orders.id].value, it[Orders.userId].value, it[Orders.totalPrice].toDouble(), it[Orders.status])
            }
        }
    }


    fun getOrderStats(): String {
        return transaction {
            val count = Orders.selectAll().count()
            val totalSum = Orders.selectAll().sumOf { it[Orders.totalPrice].toDouble() }
            "Total orders: $count, Total revenue: $totalSum"
        }
    }
}