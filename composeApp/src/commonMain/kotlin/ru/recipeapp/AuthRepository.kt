package ru.recipeapp

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.recipeapp.models.LoginRequest // Убедись, что эта модель есть в shared!

class AuthRepository {
    private val client = RecipeClient.httpClient

    // Функция возвращает true, если вход успешен, и false, если нет
    suspend fun login(login: String, pass: String): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(login = login, password = pass))
            }

            // Если сервер ответил 200 OK — значит пускаем
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Ошибка входа: ${e.message}")
            false
        }
    }
}