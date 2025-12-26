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

// Описание таблицы избранного (связка пользователя и рецепта)
object SavedRecipesTable : Table("saved_recipes") {
    val userLogin = varchar("user_login", 50).references(UsersTable.login)
    val recipeId = integer("recipe_id").references(RecipesTable.id)
    override val primaryKey = PrimaryKey(userLogin, recipeId)
}

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

        // 1. ЛЕНТА: Получение всех рецептов
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

        // 2. ИЗБРАННОЕ: Сохранить рецепт к себе в профиль
        post("/recipes/save") {
            try {
                val params = call.receive<Map<String, String>>()
                val login = params["login"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val id = params["recipeId"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest)

                transaction {
                    SavedRecipesTable.insertIgnore {
                        it[userLogin] = login
                        it[recipeId] = id
                    }
                }
                call.respond(HttpStatusCode.OK, "Рецепт сохранен")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Ошибка: ${e.message}")
            }
        }

        // 3. ПРОФИЛЬ: Получить только сохраненные рецепты пользователя
        get("/recipes/saved/{login}") {
            val login = call.parameters["login"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val saved = transaction {
                (RecipesTable innerJoin SavedRecipesTable)
                    .select { SavedRecipesTable.userLogin eq login }
                    .map {
                        Recipe(
                            id = it[RecipesTable.id],
                            title = it[RecipesTable.title],
                            ingredients = it[RecipesTable.ingredients],
                            description = it[RecipesTable.description],
                            authorLogin = it[RecipesTable.authorLogin]
                        )
                    }
            }
            call.respond(saved)
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
        // Добавили новую таблицу в список создания
        SchemaUtils.create(UsersTable, RecipesTable, SavedRecipesTable)
    }
}