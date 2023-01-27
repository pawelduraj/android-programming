package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    var userId: Int = -1,
    var name: String,
    var email: String,
    var password: String,
    var admin: Boolean
)

object Users : Table() {
    val userId = integer("user_id").autoIncrement()
    val name = varchar("name", 24)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val admin = bool("admin")

    override val primaryKey = PrimaryKey(userId)
}
