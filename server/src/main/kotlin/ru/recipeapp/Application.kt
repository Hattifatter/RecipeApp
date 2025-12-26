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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.recipeapp.models.Recipe
import ru.recipeapp.models.LoginRequest

// ВАЖНО: Если UsersTable и RecipesTable в другом файле,
// убедись, что они доступны здесь (импортированы).

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }


    initDatabase()

    routing {
        get("/") {
            call.respondText("Сервер запущен!")
        }

        // Получение всех рецептов
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

        // Вход пользователя
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

        // Регистрация пользователя
        post("/register") {
            val request = call.receive<LoginRequest>()
            val registrationResult = transaction {
                val userExists = UsersTable.select { UsersTable.login eq request.login }.any()
                if (userExists) {
                    false
                } else {
                    UsersTable.insert {
                        it[login] = request.login
                        it[password] = request.password
                    }
                    true
                }
            }

            if (registrationResult) {
                call.respond(HttpStatusCode.Created, "Регистрация успешна")
            } else {
                call.respond(HttpStatusCode.Conflict, "Пользователь уже существует")
            }
        }

        // Добавление нового рецепта
        post("/recipes") {
            try {
                val recipe = call.receive<Recipe>()
                transaction {
                    RecipesTable.insert {
                        it[title] = recipe.title
                        it[ingredients] = recipe.ingredients
                        it[description] = recipe.description
                        it[authorLogin] = recipe.authorLogin
                    }
                }
                call.respond(HttpStatusCode.Created, "Рецепт добавлен")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Ошибка формата данных: ${e.message}")
            }
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