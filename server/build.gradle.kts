plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "ru.recipeapp"
version = "1.0.0"
application {
    mainClass.set("ru.recipeapp.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)

    // Используем имена из TOML файла
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // База данных PostgreSQL (оставляем как есть, они подтянутся напрямую)
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.postgresql:postgresql:42.6.0")

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.kotlin.test)
}