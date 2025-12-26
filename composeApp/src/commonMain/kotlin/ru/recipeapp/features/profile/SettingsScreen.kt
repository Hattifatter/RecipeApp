package ru.recipeapp.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.designsystem.theme.spacing

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val s = MaterialTheme.spacing
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }

    Column(modifier.padding(s.m), verticalArrangement = Arrangement.spacedBy(s.m)) {
        Text("Настройки", style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Тёмная тема", style = MaterialTheme.typography.titleMedium)
                Text("Пока болванка", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = darkMode, onCheckedChange = { darkMode = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Уведомления", style = MaterialTheme.typography.titleMedium)
                Text("Пока болванка", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = notifications, onCheckedChange = { notifications = it })
        }
    }
}
