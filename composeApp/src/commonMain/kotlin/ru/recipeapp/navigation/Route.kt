package ru.recipeapp.navigation

enum class MainTab { Add, Menu, Favorites }

sealed interface Route {
    data object Auth : Route
    data class Main(val tab: MainTab = MainTab.Menu) : Route

    data class RecipeDetails(val recipeId: Long) : Route
    data class AddEditRecipe(val recipeId: Long? = null) : Route

    data object Settings : Route
}
