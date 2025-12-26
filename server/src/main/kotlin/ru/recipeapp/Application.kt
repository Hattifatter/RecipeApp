package ru.recipeapp

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.recipeapp.models.Recipe
import ru.recipeapp.UsersTable
import ru.recipeapp.RecipesTable

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Этот блок теперь не будет красным
    install(ContentNegotiation) {
        json()
    }

    initDatabase()

    routing {
        get("/") {
            call.respondText("Сервер запущен!")
        }

        get("/recipes") {
            val recipes = transaction {
                RecipesTable.selectAll().map {
                    Recipe(
                        id = it[RecipesTable.id],
                        title = it[RecipesTable.title],
                        ingredients = it[RecipesTable.ingredients],
                        description = it[RecipesTable.description],
                        authorEmail = it[RecipesTable.authorEmail]
                    )
                }
            }
            call.respond(recipes)
        }
    }
}

fun initDatabase() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/recipe_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "123"
    )

    transaction {
        SchemaUtils.create(UsersTable, RecipesTable)
    }
}