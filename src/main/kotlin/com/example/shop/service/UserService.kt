package com.example.shop.service

import com.example.shop.domain.RegisterRequest
import com.example.shop.domain.UserResponse
import com.example.shop.repository.Users
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt



import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.shop.domain.LoginRequest
import com.example.shop.domain.TokenResponse
import java.util.*




class UserService {
    fun register(request: RegisterRequest): UserResponse? {
        return transaction {
            // Проверяем, не занят ли email
            val existingUser = Users.select { Users.email eq request.email }.singleOrNull()
            if (existingUser != null) return@transaction null

            // Хэшируем пароль
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            // Сохраняем в базу
            val userId = Users.insertAndGetId {
                it[email] = request.email
                it[passwordHash] = hashedPassword
                it[role] = "USER"

            }

            UserResponse(userId.value, request.email, "USER")
        }
    }


    // Внутри класса UserService добавь этот метод:
    fun login(request: LoginRequest, secret: String, issuer: String, audience: String): TokenResponse? {
        val user = transaction {
            Users.select { Users.email eq request.email }.singleOrNull()
        } ?: return null

        // Проверяем пароль
        val isPasswordCorrect = BCrypt.checkpw(request.password, user[Users.passwordHash])
        if (!isPasswordCorrect) return null

        // Генерируем токен
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", user[Users.email])
            .withClaim("userId", user[Users.id].value)
            .withClaim("role", user[Users.role])
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // Срок действия 1 час
            .sign(Algorithm.HMAC256(secret))

        return TokenResponse(token)
    }






}