package ru.recipeapp.features.recipes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.recipeapp.features.recipes.data.RecipesRepository

@Composable
actual fun CreateRecipeScreen(
    repository: RecipesRepository,
    authorLogin: String,
    onCreated: (Long) -> Unit,
    modifier: Modifier
) {
    Text("Android-only screen", modifier = modifier)
}
