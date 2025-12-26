package ru.recipeapp.features.recipes.data

import ru.recipeapp.features.recipes.RecipeUi
import ru.recipeapp.features.recipes.RecipeCardUi
import ru.recipeapp.features.recipes.RecipeFilters

class FakeRecipesRepository : RecipesRepository {

    private data class Meta(
        val category: String,
        val durationText: String
    )

    // Метаданные (категория/время) по id
    private var metaById: MutableMap<Long, Meta> = mutableMapOf()

    private var full: List<RecipeUi> = seedRecipes().also { list ->
        // Заполним мету теми же id
        metaById = buildMap {
            list.forEach { r ->
                put(r.id, pickMeta(r.id))
            }
        }.toMutableMap()
    }

    private fun seedRecipes(): List<RecipeUi> {
        val base = listOf(
            RecipeUi(
                id = 1,
                title = "Горячие бутерброды",
                description = "Быстро, сытно, идеально к чаю.",
                ingredients = listOf("Хлеб", "Сыр", "Ветчина", "Помидор", "Соус"),
                author = "user_name",
                isFavorite = true
            ),
            RecipeUi(
                id = 2,
                title = "Омлет с зеленью",
                description = "Нежный омлет на завтрак за 10 минут.",
                ingredients = listOf("Яйца", "Молоко", "Зелень", "Соль", "Перец"),
                author = "fit_life",
                isFavorite = false
            ),
            RecipeUi(
                id = 3,
                title = "Паста карбонара",
                description = "Классическая карбонара без сливок.",
                ingredients = listOf("Спагетти", "Бекон", "Яйцо", "Сыр", "Перец"),
                author = "chef_roma",
                isFavorite = false
            ),
            RecipeUi(
                id = 4,
                title = "Салат Цезарь",
                description = "Хрустящий салат с курицей и соусом.",
                ingredients = listOf("Курица", "Салат", "Сухарики", "Пармезан", "Соус"),
                author = "salad_pro",
                isFavorite = true
            ),
            RecipeUi(
                id = 5,
                title = "Сырники",
                description = "Пышные сырники с золотистой корочкой.",
                ingredients = listOf("Творог", "Яйцо", "Мука", "Сахар", "Ваниль"),
                author = "grandma",
                isFavorite = false
            ),
            RecipeUi(
                id = 6,
                title = "Том-ям (упрощённый)",
                description = "Острый суп с креветками и лаймом.",
                ingredients = listOf("Бульон", "Креветки", "Лайм", "Паста том-ям", "Грибы"),
                author = "spicy_chef",
                isFavorite = false
            ),
            RecipeUi(
                id = 7,
                title = "Гречка с грибами",
                description = "Просто и вкусно, отличный гарнир.",
                ingredients = listOf("Гречка", "Грибы", "Лук", "Масло", "Соль"),
                author = "homefood",
                isFavorite = false
            ),
            RecipeUi(
                id = 8,
                title = "Борщ",
                description = "Насыщенный борщ со свёклой и мясом.",
                ingredients = listOf("Свёкла", "Капуста", "Картофель", "Мясо", "Томат"),
                author = "grandma",
                isFavorite = true
            )
        )

        // Если хочешь 20 штук — дублируем с вариациями id/автора/избранного
        val extended = buildList {
            addAll(base)
            var nextId = 9L
            while (size < 20) {
                val src = base[(nextId.toInt() - 1) % base.size]
                add(
                    src.copy(
                        id = nextId,
                        author = if (nextId % 2L == 0L) src.author else "${src.author}_2",
                        isFavorite = (nextId % 5L == 0L)
                    )
                )
                nextId++
            }
        }

        return extended
    }

    private fun pickMeta(id: Long): Meta {
        // Разные категории/время — по кругу, детерминированно
        val variants = listOf(
            Meta("Фастфуд", "15–30мин"),
            Meta("Завтрак", "10–15мин"),
            Meta("Обед", "30–60мин"),
            Meta("Ужин", ">60мин"),
            Meta("Десерт", "30–60мин"),
            Meta("Суп", ">60мин"),
            Meta("ПП", "15–30мин"),
            Meta("Салат", "10–15мин"),
        )
        return variants[((id - 1) % variants.size).toInt()]
    }

    override suspend fun getMenu(query: String, filters: RecipeFilters): List<RecipeCardUi> {
        val q = query.trim()
        var result = full

        if (q.isNotBlank()) {
            result = result.filter {
                it.title.contains(q, ignoreCase = true) ||
                        it.author.contains(q, ignoreCase = true) ||
                        it.ingredients.any { ing -> ing.contains(q, ignoreCase = true) }
            }
        }

        // Простейшая заглушка фильтра по категории
        filters.category?.let { cat ->
            result = result.filter { metaById[it.id]?.category?.equals(cat, true) == true }
        }

        return result.map { r ->
            val meta = metaById[r.id] ?: pickMeta(r.id)
            RecipeCardUi(
                id = r.id,
                authorHandle = r.author,
                title = r.title,              // ✅ теперь совпадает с деталкой
                category = meta.category,
                durationText = meta.durationText,
                isFavorite = r.isFavorite
            )
        }
    }

    override suspend fun getRecipe(id: Long): RecipeUi? = full.firstOrNull { it.id == id }

    override suspend fun toggleFavorite(id: Long) {
        full = full.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
    }
}
