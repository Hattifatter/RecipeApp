package ru.recipeapp.features.auth.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class FakeAuthRepository : AuthRepository {

    private val users: MutableMap<String, String> = mutableMapOf(
        "chef_roma" to "1234",
        "fit_life" to "1234",
        "grandma" to "1234",
    )

    override suspend fun login(login: String, password: String): AuthResult {
        val stored = users[login] ?: return AuthResult.Failure(AuthError.UserNotFound)
        return if (stored == password) {
            AuthResult.Success(login)
        } else {
            AuthResult.Failure(AuthError.WrongPassword)
        }
    }

    override suspend fun register(login: String, password: String): AuthResult {
        if (users.containsKey(login)) return AuthResult.Failure(AuthError.LoginTaken)
        users[login] = password
        return AuthResult.Success(login)
    }
}

@Composable
fun rememberFakeAuthRepository(): AuthRepository = remember { FakeAuthRepository() }
