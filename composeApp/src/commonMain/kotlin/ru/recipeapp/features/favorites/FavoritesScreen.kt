package ru.recipeapp.features.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import recipeapp.composeapp.generated.resources.Res
import recipeapp.composeapp.generated.resources.compose_multiplatform
import ru.recipeapp.designsystem.components.SearchHeaderBar
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.recipes.RecipeCardUi
import ru.recipeapp.features.recipes.RecipeFilters
import ru.recipeapp.features.recipes.components.RecipeGridCard
import ru.recipeapp.features.recipes.data.RecipesRepository
import androidx.compose.material3.Button
import ru.recipeapp.features.recipes.components.RecipeFiltersDialog

@Composable
fun FavoritesScreen(
    repository: RecipesRepository,
    userLogin: String,
    onBack: (() -> Unit)?,
    onRecipeClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filters by remember { mutableStateOf(RecipeFilters()) }
    var items by remember { mutableStateOf<List<RecipeCardUi>>(emptyList()) }
    var showFiltersDialog by remember { mutableStateOf(false) }

    val placeholderPainter = painterResource(Res.drawable.compose_multiplatform)

    // Обновляем список при смене query/filters
    LaunchedEffect(query, filters, userLogin) {
        val all = repository.getMenu(query, filters)

        // Любимое = избранное + свои рецепты
        items = all.filter { it.isFavorite || it.authorHandle.equals(userLogin, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchHeaderBar(
            query = query,
            onQueryChange = { query = it },
            placeholder = "Поиск",
            onBack = onBack,
            onFilterClick = { showFiltersDialog = true },
            hasActiveFilters = filters.isActive
        )

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет рецептов", color = AppColors.Placeholder)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 14.dp,
                    bottom = 14.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items, key = { it.id }) { r ->
                    RecipeGridCard(
                        item = r,
                        painter = placeholderPainter,
                        onClick = { onRecipeClick(r.id) }
                    )
                }
            }
        }
    }

    if (showFiltersDialog) {
        RecipeFiltersDialog(
            current = filters,
            onApply = { filters = it; showFiltersDialog = false },
            onDismiss = { showFiltersDialog = false }
        )
    }
}
