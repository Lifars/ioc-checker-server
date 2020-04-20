package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface SqlCrdRepository<
        Entity,
        Table : BaseTable
        > : CrdRepository<Long, Entity> {

    val table: Table
    val database: Database

    suspend fun ResultRow.toEntity(): Entity
    fun Table.setFields(row: UpdateBuilder<Number>, entity: Entity)

    override suspend fun findById(id: Long): Entity? = database.query {
        table.select { table.id eq id }.mapSingle()
    }

    override suspend fun find(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?
    ): Page<Entity> = database.query {
        table.select(filter, reference).orderBy(table, sort).mapPaged(pagination)
    }

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: Long
    ): Page<Entity>

    override suspend fun create(entity: Entity): Long {
        val id = database.query {
            table.insertAndGetId {
                setFields(it, entity)
            }
        }
        return id.value
    }

    override suspend fun findByIds(ids: Iterable<Long>): List<Entity> = database.query {
        table.select { table.id inList ids }.mapAll()
    }

    override suspend fun delete(id: Long): Unit = database.query {
        table.deleteWhere { table.id eq id }
        Unit
    }

    override suspend fun deleteMany(ids: Iterable<Long>): Unit = database.query {
        table.deleteWhere { table.id inList ids }
        Unit
    }

    suspend fun Query.mapPaged(
        pagination: Pagination
    ): Page<Entity> {
        return (copy() as Query).limit(pagination)
            .map { it.toEntity() }
            .asPage(totalSize = this.count(), offset = pagination.offset)
    }

    suspend fun Query.mapAll(): List<Entity> {
        return map { it.toEntity() }
    }

    suspend fun Query.mapSingle(): Entity? {
        return firstOrNull()?.toEntity()
    }
}

