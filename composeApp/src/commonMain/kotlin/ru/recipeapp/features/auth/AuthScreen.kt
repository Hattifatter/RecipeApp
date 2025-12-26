package ru.recipeapp.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.components.AppTextField
import ru.recipeapp.designsystem.components.PrimaryButton
import ru.recipeapp.designsystem.theme.spacing

private enum class AuthMode { Login, Register }

@Composable
fun AuthScreen(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing
    var mode by remember { mutableStateOf(AuthMode.Login) }

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(s.m),
        verticalArrangement = Arrangement.spacedBy(s.s)
    ) {
        Text(
            text = if (mode == AuthMode.Login) "Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineSmall
        )

        AppTextField(
            value = login,
            onValueChange = { login = it },
            label = "Логин",
            placeholder = "например: chef_roma",
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль",
            placeholder = "••••••••",
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryButton(
            text = if (mode == AuthMode.Login) "Войти" else "Создать аккаунт",
            onClick = onSuccess,
            modifier = Modifier.fillMaxWidth(),
            enabled = login.isNotBlank() && password.isNotBlank()
        )

        Spacer(Modifier.height(s.m))

        PrimaryButton(
            text = if (mode == AuthMode.Login) "Нет аккаунта? Регистрация" else "Уже есть аккаунт? Войти",
            onClick = { mode = if (mode == AuthMode.Login) AuthMode.Register else AuthMode.Login },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
