package ru.recipeapp.features.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.components.AppTextField
import ru.recipeapp.designsystem.components.PrimaryButton
import ru.recipeapp.designsystem.theme.spacing

@Composable
fun AddEditRecipeScreen(
    initial: RecipeUi? = null,
    onSave: (title: String, description: String, ingredients: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing

    var title by remember { mutableStateOf(initial?.title.orEmpty()) }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    var ingredientsText by remember {
        mutableStateOf(initial?.ingredients?.joinToString(", ").orEmpty())
    }

    Column(modifier.padding(s.m), verticalArrangement = Arrangement.spacedBy(s.s)) {
        AppTextField(
            value = title,
            onValueChange = { title = it },
            label = "Название",
            placeholder = "Например: Борщ",
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = description,
            onValueChange = { description = it },
            label = "Описание",
            placeholder = "Коротко: что за рецепт",
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = ingredientsText,
            onValueChange = { ingredientsText = it },
            label = "Ингредиенты",
            placeholder = "Вводи через запятую",
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(s.m))

        PrimaryButton(
            text = if (initial == null) "Создать" else "Сохранить",
            onClick = {
                val ingredients = ingredientsText
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                onSave(title.trim(), description.trim(), ingredients)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        )
    }
}
