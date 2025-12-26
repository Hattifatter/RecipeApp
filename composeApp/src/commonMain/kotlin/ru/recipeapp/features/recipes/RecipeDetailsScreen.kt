package ru.recipeapp.features.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.components.PrimaryButton
import ru.recipeapp.designsystem.theme.spacing

@Composable
fun RecipeDetailsScreen(
    recipe: RecipeUi,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing

    Column(modifier.padding(s.m)) {
        Text(recipe.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(s.xs))
        Text("Автор: ${recipe.author}", style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(s.m))
        Text(recipe.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(s.l))
        Text("Ингредиенты", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(s.s))
        recipe.ingredients.forEach { ing ->
            Text("• $ing", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(s.l))
        Row(horizontalArrangement = Arrangement.spacedBy(s.s)) {
            PrimaryButton(
                text = if (recipe.isFavorite) "Убрать из избранного" else "В избранное",
                onClick = onToggleFavorite,
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = "Редактировать",
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
