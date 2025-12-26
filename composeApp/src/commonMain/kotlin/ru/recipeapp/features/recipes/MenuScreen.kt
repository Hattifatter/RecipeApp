package ru.recipeapp.features.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import recipeapp.composeapp.generated.resources.Res
import recipeapp.composeapp.generated.resources.compose_multiplatform
import ru.recipeapp.designsystem.components.SearchHeaderBar
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
            onBack = onBack, // можешь передать null, если на этом экране “назад” не нужен
            onFilterClick = { showFiltersDialog = true }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 14.dp, end = 14.dp,
                top = 14.dp,
                bottom = 92.dp // чтобы карточки не прятались под нижнее меню
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

    if (showFiltersDialog) {
        AlertDialog(
            onDismissRequest = { showFiltersDialog = false },
            confirmButton = {
                TextButton(onClick = { showFiltersDialog = false }) { Text("Ок") }
            },
            title = { Text("Фильтры") },
            text = {
                Text(
                    "Пока заглушка. Потом сюда подключим реальные фильтры.\n\n" +
                            "Сейчас можно просто оставить UI-кнопку и не ломать верстку."
                )
            }
        )
    }
}
