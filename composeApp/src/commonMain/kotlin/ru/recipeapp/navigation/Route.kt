package ru.recipeapp.navigation

enum class MainTab { Recipes, Favorites, Profile }

sealed interface Route {
    data object Auth : Route
    data class Main(val tab: MainTab = MainTab.Recipes) : Route
    data class RecipeDetails(val recipeId: Long) : Route
    data class AddEditRecipe(val recipeId: Long? = null) : Route
    data object Settings : Route
}
