package ru.recipeapp.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.components.PrimaryButton
import ru.recipeapp.designsystem.theme.spacing

@Composable
fun ProfileScreen(
    userName: String,
    userLogin: String,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing

    Column(modifier.padding(s.m), verticalArrangement = Arrangement.spacedBy(s.s)) {
        Text("Профиль", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(s.s))

        Text("Имя: $userName", style = MaterialTheme.typography.bodyLarge)
        Text("Логин: @$userLogin", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(s.l))

        PrimaryButton(
            text = "Настройки",
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryButton(
            text = "Выйти",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
