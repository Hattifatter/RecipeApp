package ru.recipeapp

import androidx.compose.runtime.Composable
import ru.recipeapp.app.AppRoot
import ru.recipeapp.designsystem.theme.RecipeAppTheme

@Composable
fun App() {
    RecipeAppTheme {
        AppRoot()
    }
}
