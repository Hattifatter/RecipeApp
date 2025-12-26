package ru.recipeapp.features.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.auth.components.AuthErrorSlot
import ru.recipeapp.features.auth.components.AuthPasswordField
import ru.recipeapp.features.auth.components.AuthPillTextField
import ru.recipeapp.features.auth.components.AuthPrimaryButton
import ru.recipeapp.features.auth.data.AuthError
import ru.recipeapp.features.auth.data.AuthRepository
import ru.recipeapp.features.auth.data.AuthResult
import ru.recipeapp.features.auth.data.rememberFakeAuthRepository

private enum class AuthMode { Login, Register }

@Composable
fun AuthScreen(
    onSuccess: () -> Unit,
    repository: AuthRepository? = null
) {
    val repo = repository ?: rememberFakeAuthRepository()
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(AuthMode.Login) }

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var repeatError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }

    fun clearErrors() {
        loginError = null
        passwordError = null
        repeatError = null
    }

    fun goToLogin() {
        mode = AuthMode.Login
        clearErrors()
        repeatPassword = ""
        passwordVisible = false
    }

    fun goToRegister() {
        mode = AuthMode.Register
        clearErrors()
        repeatPassword = ""
        passwordVisible = false
    }

    fun submitLogin() {
        clearErrors()

        val l = login.trim()
        val p = password

        // локальная валидация (быстро, без “бэка”)
        var ok = true
        if (l.isBlank()) {
            loginError = "Введите логин!"
            ok = false
        }
        if (p.isBlank()) {
            passwordError = "Пароль пустой!"
            ok = false
        }
        if (!ok) return

        loading = true
        scope.launch {
            when (val r = repo.login(l, p)) {
                is AuthResult.Success -> onSuccess()
                is AuthResult.Failure -> when (r.error) {
                    AuthError.UserNotFound -> loginError = "Такого пользователя нет!"
                    AuthError.WrongPassword -> passwordError = "Неверный пароль!"
                    AuthError.LoginTaken -> loginError = "Занято!" // сюда не должно попасть на login, но пусть будет
                }
            }
            loading = false
        }
    }

    fun submitRegister() {
        clearErrors()

        val l = login.trim()
        val p = password
        val rp = repeatPassword

        var ok = true
        if (l.isBlank()) {
            loginError = "Введите логин!"
            ok = false
        }
        if (p.isBlank()) {
            passwordError = "Пароль пустой!"
            ok = false
        }
        if (rp.isBlank()) {
            repeatError = "Повторите пароль!"
            ok = false
        } else if (p.isNotBlank() && p != rp) {
            repeatError = "Пароли не совпадают!"
            ok = false
        }
        if (!ok) return

        loading = true
        scope.launch {
            when (val r = repo.register(l, p)) {
                is AuthResult.Success -> onSuccess()
                is AuthResult.Failure -> when (r.error) {
                    AuthError.LoginTaken -> loginError = "Занято!"
                    AuthError.UserNotFound -> loginError = "Такого пользователя нет!" // не актуально для регистрации
                    AuthError.WrongPassword -> passwordError = "Неверный пароль!"    // не актуально для регистрации
                }
            }
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Верхняя зона (Назад только в регистрации)
        if (mode == AuthMode.Register) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹ Назад",
                    color = AppColors.BackText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.clickable { goToLogin() }
                )
            }
            Spacer(Modifier.height(56.dp))
        } else {
            Spacer(Modifier.height(96.dp))
        }

        // Заголовок
        Text(
            text = if (mode == AuthMode.Login) "Здравствуйте!" else "Регистрация",
            color = AppColors.BrandGreen,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))

        // Ошибка логина + поле логина
        AuthErrorSlot(text = loginError)
        AuthPillTextField(
            value = login,
            onValueChange = {
                login = it
                loginError = null
            },
            placeholder = "Логин",
        )

        Spacer(Modifier.height(12.dp))

        // Ошибка пароля + поле пароля (с глазиком)
        AuthErrorSlot(text = passwordError)
        AuthPasswordField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                if (mode == AuthMode.Register) repeatError = null
            },
            placeholder = "Пароль",
            passwordVisible = passwordVisible,
            onToggleVisibility = { passwordVisible = !passwordVisible },
        )

        // Повтор пароля только в регистрации
        if (mode == AuthMode.Register) {
            Spacer(Modifier.height(12.dp))
            AuthErrorSlot(text = repeatError)
            AuthPillTextField(
                value = repeatPassword,
                onValueChange = {
                    repeatPassword = it
                    repeatError = null
                },
                placeholder = "Повторите пароль",
                singleLine = true
            )
        } else {
            // Линк “Регистрация” справа как в макете
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Регистрация",
                    color = AppColors.BrandGreen,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.clickable { goToRegister() }
                )
            }
        }

        // Толкаем кнопку вниз, чтобы было как на макете
        Spacer(Modifier.weight(1f))

        AuthPrimaryButton(
            text = if (mode == AuthMode.Login) "Войти" else "Создать",
            onClick = { if (mode == AuthMode.Login) submitLogin() else submitRegister() },
            enabled = !loading
        )

        Spacer(Modifier.height(28.dp))
    }
}
