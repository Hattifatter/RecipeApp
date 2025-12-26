import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.recipeapp.models.LoginRequest

class AuthRepository(private val client: HttpClient) {

    suspend fun login(login: String, pass: String): Boolean {
        return try {
            // Отправляем POST запрос на твой сервер
            val response = client.post("http://10.0.2.2:8080/login") { // 10.0.2.2 - это localhost для эмулятора Android
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(login = login, password = pass))
            }

            // Если сервер вернул 200 OK, значит вход разрешен
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Ошибка авторизации: ${e.message}")
            false
        }
    }
}