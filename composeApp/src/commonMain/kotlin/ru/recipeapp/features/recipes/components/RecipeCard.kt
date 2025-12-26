package ru.recipeapp.features.recipes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.theme.spacing
import ru.recipeapp.features.recipes.RecipeUi

@Composable
fun RecipeCard(
    recipe: RecipeUi,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(s.m)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(recipe.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(s.xs))
                    Text("Автор: ${recipe.author}", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onToggleFavorite) {
                    Text(if (recipe.isFavorite) "★" else "☆")
                }
            }

            Spacer(Modifier.height(s.s))
            Text(recipe.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(s.s))
            Text(
                "Ингредиенты: ${recipe.ingredients.take(3).joinToString(", ")}${if (recipe.ingredients.size > 3) "..." else ""}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
