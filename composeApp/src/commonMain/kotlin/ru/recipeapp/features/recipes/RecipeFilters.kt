package ru.recipeapp.features.recipes

enum class DurationFilter {
    Any, UpTo15, UpTo30, UpTo60, Over60
}

data class RecipeFilters(
    val category: String? = null,
    val duration: DurationFilter = DurationFilter.Any
) {
    val isActive: Boolean
        get() = category != null || duration != DurationFilter.Any
}
