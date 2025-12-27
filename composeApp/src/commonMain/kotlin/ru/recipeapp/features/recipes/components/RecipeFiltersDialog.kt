package ru.recipeapp.features.recipes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.recipes.DurationFilter
import ru.recipeapp.features.recipes.RecipeFilters

private val DefaultCategories = listOf(
    "Фастфуд", "Завтрак", "Обед", "Ужин",
    "Десерт", "Суп", "ПП", "Салат"
)

@Composable
fun RecipeFiltersDialog(
    current: RecipeFilters,
    onApply: (RecipeFilters) -> Unit,
    onDismiss: () -> Unit,
    categories: List<String> = DefaultCategories
) {
    var draft by remember(current) { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onApply(draft) }) { Text("Применить") }
        },
        dismissButton = {
            TextButton(onClick = { draft = RecipeFilters() }) { Text("Сбросить") }
        },
        title = { Text("Фильтры") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text("Категория", color = AppColors.Placeholder)

                categories.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { cat ->
                            val selected = draft.category.equals(cat, ignoreCase = true)
                            val onClick = {
                                draft = draft.copy(category = if (selected) null else cat)
                            }

                            if (selected) {
                                Button(
                                    onClick = onClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppColors.BrandGreen,
                                        contentColor = androidx.compose.ui.graphics.Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) { Text(cat) }
                            } else {
                                OutlinedButton(
                                    onClick = onClick,
                                    modifier = Modifier.weight(1f)
                                ) { Text(cat) }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text("Время", color = AppColors.Placeholder)

                val durations = listOf(
                    DurationFilter.Any to "Все",
                    DurationFilter.UpTo15 to "≤15",
                    DurationFilter.UpTo30 to "≤30",
                    DurationFilter.UpTo60 to "≤60",
                    DurationFilter.Over60 to ">60"
                )

                durations.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (d, label) ->
                            val selected = draft.duration == d
                            val onClick = { draft = draft.copy(duration = d) }

                            if (selected) {
                                Button(
                                    onClick = onClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppColors.BrandGreen,
                                        contentColor = androidx.compose.ui.graphics.Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) { Text(label) }
                            } else {
                                OutlinedButton(
                                    onClick = onClick,
                                    modifier = Modifier.weight(1f)
                                ) { Text(label) }
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    )
}
