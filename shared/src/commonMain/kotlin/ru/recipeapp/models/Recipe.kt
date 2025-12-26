package ru.recipeapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: String,
    val description: String,
    val authorLogin: String
)