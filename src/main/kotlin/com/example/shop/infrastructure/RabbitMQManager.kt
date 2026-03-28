package com.example.shop.infrastructure

import com.rabbitmq.client.ConnectionFactory

class RabbitMQManager(host: String) {
    private val factory = ConnectionFactory().apply { this.host = host }
    private val connection = factory.newConnection()
    private val channel = connection.createChannel()

    init {
        channel.queueDeclare("order_queue", true, false, false, null)
    }

    fun sendOrderMessage(message: String) {
        channel.basicPublish("", "order_queue", null, message.toByteArray())
    }
}