package ru.recipeapp.features.auth.data

sealed interface AuthError {
    data object UserNotFound : AuthError
    data object WrongPassword : AuthError
    data object LoginTaken : AuthError
}

sealed interface AuthResult {
    data class Success(val login: String) : AuthResult
    data class Failure(val error: AuthError) : AuthResult
}

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthResult
    suspend fun register(login: String, password: String): AuthResult
}
