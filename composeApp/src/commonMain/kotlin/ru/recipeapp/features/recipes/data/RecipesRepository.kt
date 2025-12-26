package ru.recipeapp.features.recipes.data

import ru.recipeapp.features.recipes.RecipeUi
import ru.recipeapp.features.recipes.RecipeCardUi
import ru.recipeapp.features.recipes.RecipeFilters

interface RecipesRepository {
    suspend fun getMenu(query: String, filters: RecipeFilters): List<RecipeCardUi>
    suspend fun getRecipe(id: Long): RecipeUi?
    suspend fun toggleFavorite(id: Long)

    suspend fun createRecipe(
        title: String,
        description: String,
        ingredients: List<String>,
        author: String
    ): Long
}
