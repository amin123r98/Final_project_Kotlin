package com.example.shop.worker

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

fun startOrderWorker(rabbitHost: String) {
    GlobalScope.launch(Dispatchers.IO) {
        val factory = ConnectionFactory().apply { host = rabbitHost }
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare("order_queue", true, false, false, null)

        val deliverCallback = DeliverCallback { _, delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            println("[WORKER] Received: '$message'")
            println("[WORKER] Sending fake email to customer... DONE")
        }
        channel.basicConsume("order_queue", true, deliverCallback) { _ -> }
    }
}