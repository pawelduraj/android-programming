package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Category(var categoryId : Int = -1, val name : String, val description : String, val keywords: String)

object Categories : Table() {
    val categoryId = integer("category_id").autoIncrement()
    val name = varchar("name", 64)
    val description = varchar("description", 256)
    val keywords = varchar("keywords", 256)

    override val primaryKey = PrimaryKey(categoryId)
}
