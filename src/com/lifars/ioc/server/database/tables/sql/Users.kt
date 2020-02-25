package com.lifars.ioc.server.database.tables.sql

import org.jetbrains.exposed.sql.`java-time`.timestamp

object Users : BaseTable("Users") {
    val name = varchar("name", 128)
    val username = varchar("username", 64)
    val email = varchar("email", 64).uniqueIndex()
    val company = varchar("company", 64)
    val password = binary("password", 60)
    val expires = timestamp("expires").nullable()
    val role = enumeration("role", Role::class)

    enum class Role {
        STANDARD,
        ADMIN
    }
}