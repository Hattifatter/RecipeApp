package ru.recipeapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int? = null,
    val title: String,
    val ingredients: String,
    val description: String,
    val authorEmail: String
)