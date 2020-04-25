package com.lifars.ioc.server.database.tables.sql

object Users : BaseTable("Users") {
    val email = varchar("email", 64).uniqueIndex()
    val role = enumeration("role", Role::class)

    enum class Role {
        IOC_CHECKER,
        SMURF_ADMIN
    }
}