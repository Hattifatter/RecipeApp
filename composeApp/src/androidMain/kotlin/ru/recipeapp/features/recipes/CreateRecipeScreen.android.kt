package ru.recipeapp.features.recipes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.recipes.data.RecipesRepository

private data class IngredientRow(var name: String = "", var amount: String = "")

@Composable
actual fun CreateRecipeScreen(
    repository: RecipesRepository,
    authorLogin: String,
    onCreated: (Long) -> Unit,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var count by remember { mutableStateOf(2) }
    var rows by remember { mutableStateOf(List(count) { IngredientRow() }) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) } // можно потом отправлять на бэк

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
            photoBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }
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

        val ingredients = rows.mapNotNull {
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
        modifier = modifier.fillMaxSize().background(Color.White)
    ) {
        Spacer(Modifier.height(18.dp))
        Text(
            "Новый рецепт",
            color = AppColors.BrandGreen,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp)
                .padding(bottom = 92.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF5B5B5B))
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoUri != null) {
                    AsyncImage(
                        model = selectedPhotoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop, // ✅ центр-кроп
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🖼", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(6.dp))
                        Text("Выберите фото", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            PillTextField(title, { title = it; titleError = null }, "Название")

            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = 18.dp).padding(top = 6.dp)
            ) {
                titleError?.let { Text(it, color = Color(0xFFEA6155), style = MaterialTheme.typography.bodySmall) }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Кол-во\nингредиентов:", color = AppColors.Placeholder, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(count.toString(), modifier = Modifier.padding(end = 10.dp))
                    Column(
                        modifier = Modifier
                            .background(AppColors.FieldBg, RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("˄", modifier = Modifier.clickable { setCount(count + 1) }, color = AppColors.Placeholder)
                        Spacer(Modifier.height(2.dp))
                        Text("˅", modifier = Modifier.clickable { setCount(count - 1) }, color = AppColors.Placeholder)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            rows.forEachIndexed { idx, r ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillTextField(
                        r.name,
                        {
                            val copy = rows.toMutableList()
                            copy[idx] = copy[idx].copy(name = it)
                            rows = copy
                        },
                        "Ингредиент",
                        modifier = Modifier.weight(1f)
                    )
                    PillTextField(
                        r.amount,
                        {
                            val copy = rows.toMutableList()
                            copy[idx] = copy[idx].copy(amount = it)
                            rows = copy
                        },
                        "Кол-во",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(14.dp))

            LargeTextField(description, { description = it }, "Ваш рецепт", Modifier.fillMaxWidth())

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { save() },
                enabled = !saving,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.BrandGreen),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (saving) "Добавление..." else "Добавить", color = Color.White)
            }

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
        placeholder = { Text(placeholder, color = AppColors.Placeholder) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.FieldBg,
            unfocusedContainerColor = AppColors.FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier.heightIn(min = 52.dp)
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
        placeholder = { Text(placeholder, color = AppColors.Placeholder) },
        minLines = 10,
        shape = RoundedCornerShape(26.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.FieldBg,
            unfocusedContainerColor = AppColors.FieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier.heightIn(min = 240.dp)
    )
}
