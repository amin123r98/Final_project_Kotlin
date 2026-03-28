package com.example.shop

import com.example.shop.domain.RegisterRequest
import com.example.shop.repository.Users
import com.example.shop.service.UserService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserServiceTest {
    private val userService = UserService()

    @Before
    fun setup() {
        // Подключаем маленькую базу данных в памяти (H2) специально для теста
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        // Создаем таблицу Users в этой виртуальной базе
        transaction {
            SchemaUtils.create(Users)
        }
    }

    @Test
    fun `test user registration`() {
        // 1. Подготавливаем данные
        val email = "test@example.com"
        val request = RegisterRequest(email, "password123")

        // 2. Вызываем нашу функцию (теперь она не упадет, так как база подключена)
        val result = userService.register(request)

        // 3. Проверяем, что вернулся правильный результат
        assertNotNull(result)
        assertEquals(email, result.email)
        assertEquals("USER", result.role)
    }
}