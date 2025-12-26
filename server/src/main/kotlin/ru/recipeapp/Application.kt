package ru.recipeapp

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.recipeapp.models.Recipe
import ru.recipeapp.models.LoginRequest


// 2. Главная функция запуска
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// 3. Основной модуль сервера
fun Application.module() {
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
                        authorLogin = it[RecipesTable.authorLogin]
                    )
                }
            }
            call.respond(recipes)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val userPassword = transaction {
                UsersTable.select { UsersTable.login eq request.login }
                    .map { it[UsersTable.password] }
                    .singleOrNull()
            }

            if (userPassword != null && userPassword == request.password) {
                call.respond(HttpStatusCode.OK, "Вход разрешен")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Неверный логин или пароль")
            }
        }
    }
}

// 4. Инициализация базы данных (объявлена ОДИН раз в конце файла)
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