package ru.recipeapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int = 0,
    val title: String,
    val ingredients: String,
    val description: String,
    val authorLogin: String,
    val imageUrl: String? = null
)