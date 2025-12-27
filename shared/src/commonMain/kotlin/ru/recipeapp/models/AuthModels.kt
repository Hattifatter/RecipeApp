package ru.recipeapp.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userEmail: String
)