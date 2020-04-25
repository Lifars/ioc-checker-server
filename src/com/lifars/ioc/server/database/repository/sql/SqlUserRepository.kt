package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.User
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlUserRepository(
    override val database: Database
) : SqlCrudRepository<User, Users>, UserRepository {

    override suspend fun findByEmail(email: String) = database.query {
        table
            .select { table.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let { it.toEntity() }
    }

    override suspend fun ResultRow.toEntity(): User = toUser()

    override fun Users.setFields(row: UpdateBuilder<Number>, entity: User) {
        row[updated] = entity.updated
        row[created] = entity.created
        row[email] = entity.email
        row[role] = entity.role.name.let { Users.Role.valueOf(it) }
    }

    override val table: Users
        get() = Users

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: Long
    ): Page<User> =
        find(pagination, filter, sort, reference)

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): User? =
        findById(id)

    override suspend fun findByIdsAndOwner(ids: Iterable<Long>, ownerId: Long): List<User> =
        findByIds(ids)
}

fun ResultRow.toUser() = User(
    id = this[Users.id].value,
    updated = this[Users.updated],
    created = this[Users.created],
    email = this[Users.email],
    role = this[Users.role].name.let { User.Role.valueOf(it) }
)