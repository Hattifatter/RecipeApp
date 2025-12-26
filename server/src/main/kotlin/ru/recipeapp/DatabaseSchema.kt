package ru.recipeapp

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 50).uniqueIndex() // Поле для логина
    val password = varchar("password", 128)        // Поле для пароля

    override val primaryKey = PrimaryKey(id)
}

object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 256)
    val ingredients = text("ingredients")
    val description = text("description")
    val authorLogin = varchar("author_login", 50) // Связываем с логином автора

    override val primaryKey = PrimaryKey(id)
}