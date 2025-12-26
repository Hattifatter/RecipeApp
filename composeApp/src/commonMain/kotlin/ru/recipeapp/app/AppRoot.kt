package ru.recipeapp.app

import ru.recipeapp.features.recipes.data.FakeRecipesRepository
import ru.recipeapp.features.recipes.data.RecipesRepository
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.recipeapp.data.SampleData
import ru.recipeapp.designsystem.components.AppTopBar
import ru.recipeapp.features.auth.AuthScreen
import ru.recipeapp.features.profile.SettingsScreen
import ru.recipeapp.features.recipes.AddEditRecipeScreen
import ru.recipeapp.features.recipes.MenuScreen
import ru.recipeapp.features.recipes.RecipeDetailsScreen
import ru.recipeapp.features.recipes.RecipeUi
import androidx.compose.runtime.produceState
import ru.recipeapp.navigation.MainTab
import ru.recipeapp.navigation.Route
import ru.recipeapp.navigation.rememberNavState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ru.recipeapp.features.recipes.CreateRecipeScreen



@Composable
fun AppRoot() {
    val nav = rememberNavState(initial = Route.Auth)

    // Заглушки данных
    val recipesRepo: RecipesRepository = remember { FakeRecipesRepository() }

    var recipes by remember { mutableStateOf(SampleData.recipes) }
    var nextId by remember { mutableStateOf((recipes.maxOfOrNull { it.id } ?: 0L) + 1L) }

    fun recipeById(id: Long): RecipeUi? = SampleData.recipeById(recipes, id)

    fun toggleFavorite(id: Long) {
        recipes = recipes.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
    }

    when (val route = nav.current) {
        Route.Auth -> {
            AuthScreen(onSuccess = { nav.reset(Route.Main()) })
        }

        is Route.Main,
        is Route.RecipeDetails,
        is Route.AddEditRecipe,
        Route.Settings -> {

            // Заголовки (для вкладки Menu он сейчас не используется, там своя шапка SearchHeaderBar)
            val topBarTitle: String = when (route) {
                is Route.Main -> when (route.tab) {
                    MainTab.Add -> "Добавить"
                    MainTab.Menu -> "Меню"
                    MainTab.Favorites -> "Любимое"
                }
                is Route.RecipeDetails -> "Рецепт"
                is Route.AddEditRecipe -> if (route.recipeId == null) "Новый рецепт" else "Редактирование"
                Route.Settings -> "Настройки"
                Route.Auth -> "" // сюда не попадаем из этой ветки
            }

            val showBottomBar = route is Route.Main

            // Вкладка Menu сама рисует свою шапку (SearchHeaderBar),
            // поэтому AppTopBar там НЕ показываем, иначе будет двойная шапка.
            val showTopBar = when (route) {
                is Route.Main -> route.tab != MainTab.Menu && route.tab != MainTab.Add
                else -> true
            }


            Scaffold(
                topBar = {
                    if (showTopBar) {
                        AppTopBar(
                            title = topBarTitle,
                            onBack = if (nav.canGoBack && route !is Route.Main) ({ nav.pop() }) else null
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        val tab = (route as Route.Main).tab
                        MainBottomBar(
                            selected = tab,
                            onSelect = { nav.replace(Route.Main(it)) }
                        )
                    }
                }
            ) { padding ->

                when (route) {
                    is Route.Main -> {
                        when (route.tab) {
                            MainTab.Menu -> MenuScreen(
                                repository = recipesRepo,
                                onBack = null,
                                onRecipeClick = { id -> nav.navigate(Route.RecipeDetails(id)) },
                                modifier = Modifier.padding(padding)
                            )

                            MainTab.Add -> CreateRecipeScreen(
                                repository = recipesRepo,
                                authorLogin = SampleData.userLogin, // пока так; потом возьмёшь из auth
                                onCreated = { _ ->
                                    nav.replace(Route.Main(MainTab.Menu)) // после добавления кидаем в меню
                                },
                                modifier = Modifier.padding(padding)
                            )


                            MainTab.Favorites -> {
                                // пока заглушка
                                Text("Любимое — позже", modifier = Modifier.padding(padding))
                            }
                        }
                    }

                    is Route.RecipeDetails -> {
                        var recipe by remember(route.recipeId) { mutableStateOf<RecipeUi?>(null) }

                        LaunchedEffect(route.recipeId) {
                            recipe = recipesRepo.getRecipe(route.recipeId)  // suspend
                        }

                        if (recipe == null) {
                            Text("Загрузка...", modifier = Modifier.padding(padding))
                        } else {
                            RecipeDetailsScreen(
                                recipe = recipe!!,
                                onEdit = { nav.navigate(Route.AddEditRecipe(recipe!!.id)) },
                                onToggleFavorite = { /* позже: recipesRepo.toggleFavorite(recipe!!.id) */ },
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
