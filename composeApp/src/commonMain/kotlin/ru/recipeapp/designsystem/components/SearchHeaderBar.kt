package ru.recipeapp.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.recipeapp.designsystem.theme.AppColors

@Composable
fun SearchHeaderBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onBack: (() -> Unit)?,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(34.dp))
            // Поиск
            TextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = {
                    Text(
                        placeholder,
                        color = AppColors.Placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                },
                singleLine = true,
                leadingIcon = {
                    // Иконка-заглушка (потом поменяешь на нормальную)
                    Text("🔍")
                },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color.White, CircleShape)
                                .clickable { onQueryChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "×",
                                color = AppColors.InactiveGray,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                },
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
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
            )

            Spacer(Modifier.width(10.dp))

            // Фильтр (заглушка)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☰",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                if (hasActiveFilters) {
                    // маленькая точка-индикатор
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .background(ru.recipeapp.designsystem.theme.AppColors.BrandGreen, CircleShape)
                    )
                }
            }

        }

        HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
    }
}
