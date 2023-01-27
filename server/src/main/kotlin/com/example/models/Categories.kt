package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Category(
    var categoryId: Int = -1,
    var name: String
)

object Categories : Table() {
    val categoryId = integer("category_id").autoIncrement()
    val name = varchar("name", 64)

    override val primaryKey = PrimaryKey(categoryId)
}
