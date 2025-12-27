package ru.recipeapp

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 50).uniqueIndex()
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}

object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 256)
    val ingredients = text("ingredients")
    val description = text("description")
    // Указываем, что автор должен существовать в таблице пользователей
    val authorLogin = varchar("author_login", 50).references(UsersTable.login)
    val imageUrl = varchar("image_url", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

// НОВАЯ ТАБЛИЦА: для реализации функции "сохранить к себе"
object SavedRecipesTable : Table("saved_recipes") {
    // Кто сохранил
    val userLogin = varchar("user_login", 50).references(UsersTable.login)
    // Что сохранил
    val recipeId = integer("recipe_id").references(RecipesTable.id)

    // Пара (юзер + рецепт) должна быть уникальной, чтобы не сохранять одно и то же дважды
    override val primaryKey = PrimaryKey(userLogin, recipeId)
}