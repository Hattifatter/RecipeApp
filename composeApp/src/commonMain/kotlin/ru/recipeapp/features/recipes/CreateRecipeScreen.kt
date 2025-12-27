package ru.recipeapp.features.recipes

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ImageBitmap
import ru.recipeapp.platform.decodeImageBitmap
import ru.recipeapp.platform.rememberImagePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.recipes.data.RecipesRepository

private data class IngredientRow(var name: String = "", var amount: String = "")

@Composable
fun CreateRecipeScreen(
    repository: RecipesRepository,
    authorLogin: String,
    onCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var count by remember { mutableStateOf(2) }
    var rows by remember { mutableStateOf(List(count) { IngredientRow() }) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }

    val photoBitmap: ImageBitmap? = remember(photoBytes) {
        photoBytes?.let { decodeImageBitmap(it) }
    }


    val imagePicker = rememberImagePicker { bytes ->
        photoBytes = bytes
    }


    fun setCount(newCount: Int) {
        val c = newCount.coerceIn(1, 20)
        count = c
        rows = when {
            rows.size < c -> rows + List(c - rows.size) { IngredientRow() }
            rows.size > c -> rows.take(c)
            else -> rows
        }
    }

    fun save() {
        titleError = null
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) {
            titleError = "Введите название"
            return
        }

        val ingredients = rows
            .mapNotNull {
                val n = it.name.trim()
                val a = it.amount.trim()
                if (n.isBlank() && a.isBlank()) null
                else if (a.isBlank()) n
                else "$n — $a"
            }

        saving = true
        scope.launch {
            val id = repository.createRecipe(
                title = cleanTitle,
                description = description.trim(),
                ingredients = ingredients.ifEmpty { listOf("—") },
                author = authorLogin
            )
            saving = false
            onCreated(id)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Заголовок как на шаблоне
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Новый рецепт",
            color = AppColors.BrandGreen,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)

        // Контент (скролл)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp)
                .padding(bottom = 92.dp), // чтобы не залезало под нижнее меню
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))

            // Фото-заглушка (кликабельно)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF5B5B5B))
                    .clickable { imagePicker.launch() },
                contentAlignment = Alignment.Center
            ) {
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap!!,
                        contentDescription = null,
                        contentScale = ContentScale.Crop, // ✅ центр-кроп
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🖼", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Выберите фото",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }


            Spacer(Modifier.height(18.dp))

            // Название (пилюля)
            PillTextField(
                value = title,
                onValueChange = { title = it; titleError = null },
                placeholder = "Название"
            )

            // Ошибка (если надо)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 18.dp)
                    .padding(top = 6.dp)
            ) {
                if (titleError != null) {
                    Text(titleError!!, color = Color(0xFFEA6155), style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Кол-во ингредиентов + степпер
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Кол-во\nингредиентов:",
                    color = AppColors.Placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = count.toString(),
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Column(
                        modifier = Modifier
                            .background(AppColors.FieldBg, RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "˄",
                            modifier = Modifier.clickable { setCount(count + 1) },
                            color = AppColors.Placeholder,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "˅",
                            modifier = Modifier.clickable { setCount(count - 1) },
                            color = AppColors.Placeholder,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Ингредиенты (2 колонки как на шаблоне)
            rows.forEachIndexed { idx, r ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PillTextField(
                        value = r.name,
                        onValueChange = {
                            val copy = rows.toMutableList()
                            copy[idx] = copy[idx].copy(name = it)
                            rows = copy
                        },
                        placeholder = "Ингредиент",
                        modifier = Modifier.weight(1f)
                    )
                    PillTextField(
                        value = r.amount,
                        onValueChange = {
                            val copy = rows.toMutableList()
                            copy[idx] = copy[idx].copy(amount = it)
                            rows = copy
                        },
                        placeholder = "Кол-во",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(14.dp))

            // Большое поле "Ваш рецепт"
            LargeTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Ваш рецепт",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Кнопка Добавить
            PrimaryPillButton(
                text = if (saving) "Добавление..." else "Добавить",
                enabled = !saving,
                onClick = { save() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = AppColors.Placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.FieldBg,
            unfocusedContainerColor = AppColors.FieldBg,
            disabledContainerColor = AppColors.FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = AppColors.BrandGreen
        ),
        modifier = modifier
            .heightIn(min = 52.dp)
    )
}

@Composable
private fun LargeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = AppColors.Placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        },
        singleLine = false,
        minLines = 10,
        shape = RoundedCornerShape(26.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.FieldBg,
            unfocusedContainerColor = AppColors.FieldBg,
            disabledContainerColor = AppColors.FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = AppColors.BrandGreen
        ),
        modifier = modifier
            .heightIn(min = 240.dp)
    )
}

@Composable
private fun PrimaryPillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.BrandGreen,
            contentColor = Color.White,
            disabledContainerColor = AppColors.BrandGreen.copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.9f),
        ),
        modifier = modifier.height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
    }
}
