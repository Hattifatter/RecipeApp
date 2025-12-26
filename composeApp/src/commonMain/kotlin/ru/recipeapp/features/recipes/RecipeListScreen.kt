package ru.recipeapp.features.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.components.AppTextField
import ru.recipeapp.designsystem.theme.spacing
import ru.recipeapp.features.recipes.components.RecipeCard

@Composable
fun RecipeListScreen(
    recipes: List<RecipeUi>,
    onRecipeClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing
    var query by remember { mutableStateOf("") }

    Column(modifier.padding(s.m)) {
        AppTextField(
            value = query,
            onValueChange = { query = it },
            label = "Поиск",
            placeholder = "Название, ингредиенты…",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(s.m))

        val filtered = remember(recipes, query) {
            if (query.isBlank()) recipes
            else recipes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.ingredients.any { ing -> ing.contains(query, ignoreCase = true) }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(s.s),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { r ->
                RecipeCard(
                    recipe = r,
                    onClick = { onRecipeClick(r.id) },
                    onToggleFavorite = { onToggleFavorite(r.id) }
                )
            }
        }
    }
}
