package ru.recipeapp.data

import ru.recipeapp.features.recipes.RecipeUi

object SampleData {

    val userName: String = "Гость"
    val userLogin: String = "guest"

    val recipes: List<RecipeUi> = listOf(
        RecipeUi(
            id = 1,
            title = "Паста карбонара",
            description = "Классика: сливочный вкус, бекон, сыр.",
            ingredients = listOf("Спагетти", "Бекон", "Яйцо", "Сыр", "Перец"),
            author = "chef_roma",
            isFavorite = true
        ),
        RecipeUi(
            id = 2,
            title = "Овсянка с фруктами",
            description = "Быстрый завтрак на каждый день.",
            ingredients = listOf("Овсянка", "Молоко", "Банан", "Ягоды", "Мёд"),
            author = "fit_life",
            isFavorite = false
        ),
        RecipeUi(
            id = 3,
            title = "Борщ",
            description = "Насыщенный суп со свёклой и мясом.",
            ingredients = listOf("Свёкла", "Капуста", "Картофель", "Мясо", "Томат"),
            author = "grandma",
            isFavorite = false
        )
    )

    fun recipeById(list: List<RecipeUi>, id: Long): RecipeUi? = list.firstOrNull { it.id == id }
}
