@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package ru.recipeapp.features.recipes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.recipeapp.features.recipes.DurationFilter
import ru.recipeapp.features.recipes.RecipeFilters

@Composable
fun FiltersDialog(
    current: RecipeFilters,
    categories: List<String>,
    onApply: (RecipeFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember(current) { mutableStateOf(current.category) }
    var selectedDuration by remember(current) { mutableStateOf(current.duration) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Фильтры") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Text("Категория", style = MaterialTheme.typography.titleMedium)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "Все" (снимает категорию)
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Все") }
                    )

                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Text("Время", style = MaterialTheme.typography.titleMedium)

                val durationOptions = listOf(
                    DurationFilter.Any to "Любое",
                    DurationFilter.UpTo15 to "10–15 мин",
                    DurationFilter.UpTo30 to "15–30 мин",
                    DurationFilter.UpTo60 to "30–60 мин",
                    DurationFilter.Over60 to "> 60 мин"
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    durationOptions.forEach { (value, label) ->
                        FilterChip(
                            selected = selectedDuration == value,
                            onClick = { selectedDuration = value },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        RecipeFilters(
                            category = selectedCategory,
                            duration = selectedDuration
                        )
                    )
                }
            ) { Text("Применить") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    selectedCategory = null
                    selectedDuration = DurationFilter.Any
                    onApply(RecipeFilters()) // мгновенный сброс
                }
            ) { Text("Сбросить") }
        }
    )
}
