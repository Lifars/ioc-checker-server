package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.UserWithPassword
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.database.tables.Users
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlUserRepository @KtorExperimentalAPI constructor(
    override val database: Database
) : SqlCrudRepository<UserWithPassword, Users>, UserRepository {

    override suspend fun findByUsername(username: String): UserWithPassword? = database.query {
        table.select {
            table.username eq username
        }.limit(1)
            .firstOrNull()
            ?.let { it.toEntity() }
    }

    override suspend fun findByUsernameOrEmail(usernameOrEmail: String): UserWithPassword? = database.query {
        table.select {
            (table.email eq usernameOrEmail) or (table.username eq usernameOrEmail)
        }.limit(1)
            .firstOrNull()
            ?.let { it.toEntity() }
    }


    override suspend fun findByEmail(email: String) = database.query {
        table
            .select { table.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let { it.toEntity() }
    }

    override suspend fun ResultRow.toEntity(): UserWithPassword = toUser()

    override fun Users.setFields(row: UpdateBuilder<Number>, entity: UserWithPassword) {
        row[name] = entity.name
        row[updated] = entity.updated
        row[created] = entity.created
        row[expires] = entity.expires
        row[email] = entity.email
        row[company] = entity.company
        row[username] = entity.username
        row[role] = entity.role.name.let { Users.Role.valueOf(it) }
        row[password] = entity.password
    }

    override val table: Users
        get() = Users

}

fun ResultRow.toUser() = UserWithPassword(
    id = this[Users.id].value,
    name = this[Users.name],
    updated = this[Users.updated],
    created = this[Users.created],
    expires = this[Users.expires],
    email = this[Users.email],
    company = this[Users.company],
    username = this[Users.username],
    role = this[Users.role].name.let { UserWithPassword.Role.valueOf(it) },
    password = this[Users.password]
)