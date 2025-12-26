package ru.recipeapp.features.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.theme.spacing
import ru.recipeapp.features.recipes.RecipeUi
import ru.recipeapp.features.recipes.components.RecipeCard

@Composable
fun FavoritesScreen(
    favorites: List<RecipeUi>,
    onRecipeClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing

    if (favorites.isEmpty()) {
        Column(modifier.fillMaxSize().padding(s.m)) {
            Text("Пока нет избранных рецептов", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(s.s))
            Text("Добавь рецепт в избранное, чтобы он появился здесь.")
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(s.m),
        verticalArrangement = Arrangement.spacedBy(s.s)
    ) {
        items(favorites, key = { it.id }) { r ->
            RecipeCard(
                recipe = r,
                onClick = { onRecipeClick(r.id) },
                onToggleFavorite = { onToggleFavorite(r.id) }
            )
        }
    }
}
