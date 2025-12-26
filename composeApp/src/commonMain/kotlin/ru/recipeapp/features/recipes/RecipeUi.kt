package ru.recipeapp.features.recipes

data class RecipeUi(
    val id: Long,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val author: String,
    val isFavorite: Boolean
)
