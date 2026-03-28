package com.example.shop.service

import com.example.shop.domain.*
import com.example.shop.repository.Products
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import redis.clients.jedis.JedisPool

class ProductService(private val redisPool: JedisPool) {

    fun getAllProducts(): List<ProductResponse> {
        return transaction {
            Products.selectAll().map {
                ProductResponse(
                    it[Products.id].value,
                    it[Products.name],
                    it[Products.price].toDouble(),
                    it[Products.stock]
                )
            }
        }
    }

    fun getProductById(id: Int): ProductResponse? {
        // 1. Пробуем Redis
        val cached = try {
            redisPool.resource.use { it.get("product:$id") }
        } catch (e: Exception) { null }

        if (cached != null) {
            return Json.decodeFromString<ProductResponse>(cached)
        }

        // 2. Идем в БД
        val product = transaction {
            Products.selectAll().where { Products.id eq id }.map {
                ProductResponse(
                    it[Products.id].value,
                    it[Products.name],
                    it[Products.price].toDouble(),
                    it[Products.stock]
                )
            }.singleOrNull()
        }

        // 3. Сохраняем в Redis
        product?.let { p ->
            try {
                redisPool.resource.use { it.setex("product:$id", 300, Json.encodeToString(p)) }
            } catch (e: Exception) { /* ignore */ }
        }

        return product
    }

    fun createProduct(request: ProductRequest): ProductResponse {
        return transaction {
            val generatedId = Products.insertAndGetId {
                it[name] = request.name
                it[price] = request.price.toBigDecimal()
                it[stock] = request.stock
            }
            ProductResponse(generatedId.value, request.name, request.price, request.stock)
        }
    }
}