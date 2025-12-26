package ru.recipeapp.app

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.recipeapp.navigation.MainTab

@Composable
fun MainBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == MainTab.Recipes,
            onClick = { onSelect(MainTab.Recipes) },
            icon = { Text("🍲") },
            label = { Text("Рецепты") }
        )
        NavigationBarItem(
            selected = selected == MainTab.Favorites,
            onClick = { onSelect(MainTab.Favorites) },
            icon = { Text("★") },
            label = { Text("Избранное") }
        )
        NavigationBarItem(
            selected = selected == MainTab.Profile,
            onClick = { onSelect(MainTab.Profile) },
            icon = { Text("👤") },
            label = { Text("Профиль") }
        )
    }
}
