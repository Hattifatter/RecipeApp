package ru.recipeapp.features.recipes

data class RecipeCardUi(
    val id: Long,
    val authorHandle: String,   // user_name
    val title: String,          // Горячие бутерброды
    val category: String,       // Фастфуд
    val durationText: String,   // >60мин
    val isFavorite: Boolean = false
)
