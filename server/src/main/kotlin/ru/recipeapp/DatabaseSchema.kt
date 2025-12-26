package ru.recipeapp

import org.jetbrains.exposed.sql.Table

// Таблица пользователей для регистрации/входа
object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 128).uniqueIndex()
    val password = varchar("password", 128)
    override val primaryKey = PrimaryKey(id)
}

// Таблица рецептов
object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 256)
    val description = text("description")
    val ingredients = text("ingredients")
    val authorEmail = varchar("author_email", 128)
    override val primaryKey = PrimaryKey(id)
}