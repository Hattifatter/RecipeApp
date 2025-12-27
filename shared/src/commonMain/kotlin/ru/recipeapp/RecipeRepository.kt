package ru.recipeapp.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.recipeapp.models.Recipe
import io.ktor.http.*

class RecipeRepository(private val client: HttpClient) {
    suspend fun getRecipes(): List<Recipe> {
        return try {
            // Запрашиваем список рецептов у сервера
            client.get("http://10.0.2.2:8080/recipes").body()
        } catch (e: Exception) {
            emptyList() // Если сервер спит, возвращаем пустой список
        }
    }
    suspend fun addRecipe(recipe: Recipe): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/recipes") {
                contentType(ContentType.Application.Json)
                setBody(recipe)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            false
        }
    }
    // Сохранить рецепт
    suspend fun saveRecipe(login: String, recipeId: Int): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/recipes/save") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("login" to login, "recipeId" to recipeId.toString()))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    // Получить список избранного
    suspend fun getSavedRecipes(login: String): List<Recipe> {
        return try {
            client.get("http://10.0.2.2:8080/recipes/saved/$login").body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}