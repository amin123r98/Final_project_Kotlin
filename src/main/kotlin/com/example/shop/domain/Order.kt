package com.example.shop.domain

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(val productId: Int, val quantity: Int)

@Serializable
data class OrderRequest(val items: List<OrderItemRequest>)

@Serializable
data class OrderResponse(val id: Int, val userId: Int, val totalPrice: Double, val status: String)