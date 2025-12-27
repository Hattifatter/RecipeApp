package ru.recipeapp

import org.jetbrains.exposed.sql.Table

// 1. Таблица тегов (Категории: Завтрак, Обед, Ужин и т.д.)
object TagsTable : Table("tags") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex() // Название тега должно быть уникальным

    override val primaryKey = PrimaryKey(id)
}

// 2. Таблица пользователей
object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 50).uniqueIndex()
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}

// 3. Таблица рецептов
object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 256)
    val ingredients = text("ingredients")
    val description = text("description")
    val authorLogin = varchar("author_login", 50).references(UsersTable.login)
    val imageUrl = varchar("image_url", 500).nullable()

    // Ссылка на ID тега из таблицы TagsTable
    val tagId = integer("tag_id").references(TagsTable.id).nullable()

    override val primaryKey = PrimaryKey(id)
}

// 4. Таблица избранного
object SavedRecipesTable : Table("saved_recipes") {
    val userLogin = varchar("user_login", 50).references(UsersTable.login)
    val recipeId = integer("recipe_id").references(RecipesTable.id)

    override val primaryKey = PrimaryKey(userLogin, recipeId)
}