package ru.recipeapp.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.data.SampleData
import ru.recipeapp.designsystem.components.AppTopBar
import ru.recipeapp.features.auth.AuthScreen
import ru.recipeapp.features.favorites.FavoritesScreen
import ru.recipeapp.features.profile.ProfileScreen
import ru.recipeapp.features.profile.SettingsScreen
import ru.recipeapp.features.recipes.AddEditRecipeScreen
import ru.recipeapp.features.recipes.RecipeDetailsScreen
import ru.recipeapp.features.recipes.RecipeListScreen
import ru.recipeapp.features.recipes.RecipeUi
import ru.recipeapp.navigation.MainTab
import ru.recipeapp.navigation.Route
import ru.recipeapp.navigation.rememberNavState

@Composable
fun AppRoot() {
    // Для быстрого старта можешь поставить Route.Main(), а Auth включить потом:
    val nav = rememberNavState(initial = Route.Main())

    var recipes by remember { mutableStateOf(SampleData.recipes) }
    var nextId by remember { mutableStateOf((recipes.maxOfOrNull { it.id } ?: 0L) + 1L) }

    fun recipeById(id: Long): RecipeUi? = SampleData.recipeById(recipes, id)

    fun toggleFavorite(id: Long) {
        recipes = recipes.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
    }

    when (val route = nav.current) {
        Route.Auth -> {
            AuthScreen(
                onSuccess = { nav.reset(Route.Main()) }
            )
        }

        is Route.Main,
        is Route.RecipeDetails,
        is Route.AddEditRecipe,
        Route.Settings -> {

            val topBarTitle = when (route) {
                is Route.Main -> when (route.tab) {
                    MainTab.Recipes -> "Рецепты"
                    MainTab.Favorites -> "Избранное"
                    MainTab.Profile -> "Профиль"
                }
                is Route.RecipeDetails -> "Рецепт"
                is Route.AddEditRecipe -> if (route.recipeId == null) "Новый рецепт" else "Редактирование"
                Route.Settings -> "Настройки"
                Route.Auth -> "" // сюда не попадём
            }

            val showBottomBar = route is Route.Main
            val showFab = route is Route.Main && route.tab == MainTab.Recipes

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = topBarTitle,
                        onBack = if (nav.canGoBack && route !is Route.Main) ({ nav.pop() }) else null,
                        actions = {
                            if (route is Route.Main && route.tab == MainTab.Profile) {
                                TextButton(onClick = { nav.navigate(Route.Settings) }) {
                                    Text("⚙")
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    if (showBottomBar) {
                        val tab = (route as Route.Main).tab
                        MainBottomBar(
                            selected = tab,
                            onSelect = { nav.replace(Route.Main(it)) }
                        )
                    }
                },
                floatingActionButton = {
                    if (showFab) {
                        FloatingActionButton(onClick = { nav.navigate(Route.AddEditRecipe()) }) {
                            Text("+")
                        }
                    }
                }
            ) { padding ->
                when (route) {
                    is Route.Main -> when (route.tab) {
                        MainTab.Recipes -> RecipeListScreen(
                            recipes = recipes,
                            onRecipeClick = { nav.navigate(Route.RecipeDetails(it)) },
                            onToggleFavorite = { toggleFavorite(it) },
                            modifier = Modifier.padding(padding)
                        )

                        MainTab.Favorites -> FavoritesScreen(
                            favorites = recipes.filter { it.isFavorite },
                            onRecipeClick = { nav.navigate(Route.RecipeDetails(it)) },
                            onToggleFavorite = { toggleFavorite(it) },
                            modifier = Modifier.padding(padding)
                        )

                        MainTab.Profile -> ProfileScreen(
                            userName = SampleData.userName,
                            userLogin = SampleData.userLogin,
                            onOpenSettings = { nav.navigate(Route.Settings) },
                            onLogout = { nav.reset(Route.Auth) },
                            modifier = Modifier.padding(padding)
                        )
                    }

                    is Route.RecipeDetails -> {
                        val recipe = recipeById(route.recipeId)
                        if (recipe == null) {
                            Text("Рецепт не найден", modifier = Modifier.padding(padding))
                        } else {
                            RecipeDetailsScreen(
                                recipe = recipe,
                                onEdit = { nav.navigate(Route.AddEditRecipe(recipe.id)) },
                                onToggleFavorite = { toggleFavorite(recipe.id) },
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }

                    is Route.AddEditRecipe -> {
                        val initial = route.recipeId?.let(::recipeById)
                        AddEditRecipeScreen(
                            initial = initial,
                            onSave = { title, description, ingredients ->
                                if (initial == null) {
                                    val new = RecipeUi(
                                        id = nextId++,
                                        title = title,
                                        description = description,
                                        ingredients = ingredients,
                                        author = SampleData.userLogin,
                                        isFavorite = false
                                    )
                                    recipes = listOf(new) + recipes
                                } else {
                                    recipes = recipes.map {
                                        if (it.id == initial.id) it.copy(
                                            title = title,
                                            description = description,
                                            ingredients = ingredients
                                        ) else it
                                    }
                                }
                                nav.pop()
                            },
                            modifier = Modifier.padding(padding)
                        )
                    }

                    Route.Settings -> SettingsScreen(modifier = Modifier.padding(padding))

                    Route.Auth -> Unit
                }
            }
        }
    }
}
