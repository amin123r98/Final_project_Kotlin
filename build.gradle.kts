plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    // ДОБАВЬ ЭТУ СТРОКУ НИЖЕ:
    kotlin("plugin.serialization") version "2.1.0"

}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}



ktor {
    fatJar {
        archiveFileName.set("shop.jar")
    }
}

dependencies {
    // Ktor Core & Plugins
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Database: PostgreSQL, HikariCP, Exposed, Flyway
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jetbrains.exposed:exposed-core:0.47.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.47.0")
    implementation("org.flywaydb:flyway-core:10.8.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")

    // Redis
    implementation("redis.clients:jedis:5.1.0")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.20.0")

    // Security (Password Hashing)
    implementation("org.mindrot:jbcrypt:0.4")

    // Swagger UI
    implementation("io.github.smiley4:ktor-swagger-ui:3.0.0")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")



    //yaml формат
    implementation("io.ktor:ktor-server-config-yaml-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm") // Плагин для работы с контентом
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")


    implementation("io.github.smiley4:ktor-swagger-ui:3.3.0")
    testImplementation("com.h2database:h2:2.2.224")


    implementation("io.ktor:ktor-server-openapi-jvm")
}
