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
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import java.io.File


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

        // 1. Делаем папку "uploads" доступной извне по ссылке
        static("/uploads") {
            files("uploads")
        }

        // 2. Эндпоинт для загрузки фото из галереи
        post("/upload-image") {
            val multipart = call.receiveMultipart()
            var fileName = ""

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    // Создаем уникальное имя файла
                    val name = "${System.currentTimeMillis()}-${part.originalFileName}"
                    val fileBytes = part.streamProvider().readBytes()

                    // Создаем папку, если ее нет
                    val folder = File("uploads")
                    if (!folder.exists()) folder.mkdirs()

                    // Сохраняем байты в файл
                    File("uploads/$name").writeBytes(fileBytes)
                    fileName = "/uploads/$name" // Относительный путь для БД
                }
                part.dispose()
            }

            if (fileName.isNotEmpty()) {
                call.respond(mapOf("imageUrl" to fileName))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Файл не получен")
            }
        }

        // ЛЕНТА
        get("/recipes") {
            val recipes = transaction {
                RecipesTable.selectAll().map {
                    Recipe(
                        id = it[RecipesTable.id],
                        title = it[RecipesTable.title],
                        ingredients = it[RecipesTable.ingredients],
                        description = it[RecipesTable.description],
                        authorLogin = it[RecipesTable.authorLogin],
                        imageUrl = it[RecipesTable.imageUrl],
                        tagId = it[RecipesTable.tagId]
                    )
                }
            }
            call.respond(recipes)
        }

        // ИЗБРАННОЕ
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

        // ПРОФИЛЬ
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
                            authorLogin = it[RecipesTable.authorLogin],
                            imageUrl = it[RecipesTable.imageUrl],
                            tagId = it[RecipesTable.tagId]
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
                        it[imageUrl] = recipe.imageUrl
                        it[tagId] = recipe.tagId
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
        SchemaUtils.create(UsersTable, RecipesTable, SavedRecipesTable)
    }
}