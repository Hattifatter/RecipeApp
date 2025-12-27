package ru.recipeapp.features.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import ru.recipeapp.features.recipes.components.FiltersDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import recipeapp.composeapp.generated.resources.Res
import recipeapp.composeapp.generated.resources.compose_multiplatform
import ru.recipeapp.designsystem.components.SearchHeaderBar
import ru.recipeapp.features.recipes.components.RecipeFiltersDialog
import ru.recipeapp.features.recipes.components.RecipeGridCard
import ru.recipeapp.features.recipes.data.RecipesRepository

@Composable
fun MenuScreen(
    repository: RecipesRepository,
    onBack: (() -> Unit)?,
    onRecipeClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var query by rememberSaveable { mutableStateOf("") }
    var filters by remember { mutableStateOf(RecipeFilters()) }
    val placeholderPainter = painterResource(Res.drawable.compose_multiplatform)

    var items by remember { mutableStateOf<List<RecipeCardUi>>(emptyList()) }
    var showFiltersDialog by remember { mutableStateOf(false) }

    // Загрузка/обновление списка при смене query/filters
    LaunchedEffect(query, filters) {
        items = repository.getMenu(query, filters)
    }

    Column(modifier.fillMaxSize()) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет рецептов",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 14.dp,
                    bottom = 92.dp
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
