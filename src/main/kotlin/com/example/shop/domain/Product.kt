package com.example.shop.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val name: String,
    val price: Double,
    val stock: Int
)

@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int
)