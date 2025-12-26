package ru.recipeapp.app

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.navigation.MainTab

@Composable
fun MainBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColors.BrandGreen,
            selectedTextColor = AppColors.BrandGreen,
            unselectedIconColor = AppColors.InactiveGray,
            unselectedTextColor = AppColors.InactiveGray,
            indicatorColor = Color.Transparent
        )

        NavigationBarItem(
            selected = selected == MainTab.Add,
            onClick = { onSelect(MainTab.Add) },
            icon = { Text("✎") }, // потом заменишь на иконку
            label = { Text("Добавить") },
            colors = colors
        )

        NavigationBarItem(
            selected = selected == MainTab.Menu,
            onClick = { onSelect(MainTab.Menu) },
            icon = { Text("⌂") },
            label = { Text("Меню") },
            colors = colors
        )

        NavigationBarItem(
            selected = selected == MainTab.Favorites,
            onClick = { onSelect(MainTab.Favorites) },
            icon = { Text("★") },
            label = { Text("Любимое") },
            colors = colors
        )
    }
}
