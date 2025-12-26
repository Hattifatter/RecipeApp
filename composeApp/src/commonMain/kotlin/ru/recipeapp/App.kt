package ru.recipeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Используем Material3 как в твоем Gradle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        // Состояние: вошел пользователь или нет
        var isLoggedIn by remember { mutableStateOf(false) }

        if (isLoggedIn) {
            // ЭКРАН ПОСЛЕ ВХОДА
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ура! Вы успешно вошли в систему!", style = MaterialTheme.typography.headlineMedium)
            }
        } else {
            // ЭКРАН ВХОДА
            LoginScreen(onLoginSuccess = { isLoggedIn = true })
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val repository = remember { AuthRepository() }
    val scope = rememberCoroutineScope() // Для запуска сетевых запросов

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вход", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Логин") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") })
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    val isSuccess = repository.login(login, password)
                    isLoading = false

                    if (isSuccess) {
                        onLoginSuccess()
                    } else {
                        errorMessage = "Неверный логин или пароль"
                    }
                }
            }) {
                Text("Войти")
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}