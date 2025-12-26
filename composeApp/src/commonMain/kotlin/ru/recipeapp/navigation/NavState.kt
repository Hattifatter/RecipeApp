package ru.recipeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Stable
class NavState(initial: Route) {
    private val backStack = mutableStateListOf(initial)

    val current: Route get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    fun navigate(route: Route) {
        backStack.add(route)
    }

    fun pop() {
        if (canGoBack) backStack.removeAt(backStack.lastIndex)
    }

    fun replace(route: Route) {
        backStack[backStack.lastIndex] = route
    }

    fun reset(route: Route) {
        backStack.clear()
        backStack.add(route)
    }
}

@Composable
fun rememberNavState(initial: Route): NavState = remember { NavState(initial) }
