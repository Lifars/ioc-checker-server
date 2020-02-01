package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.BaseTable
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface SqlCrdRepository<
        Entity,
        Table : BaseTable
        > : CrdRepository<Entity> {

    val table: Table
    @KtorExperimentalAPI
    val database: Database

    suspend fun ResultRow.toEntity(): Entity
    fun Table.setFields(row: UpdateBuilder<Number>, entity: Entity)

    @KtorExperimentalAPI
    override suspend fun findById(id: Long): Entity? = database.query {
        table.select { table.id eq id }.mapSingle()
    }

    @KtorExperimentalAPI
    override suspend fun find(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?
    ): Page<Entity> = database.query {
        table.select(filter, reference).orderBy(table, sort).mapPaged(pagination)
    }

    @KtorExperimentalAPI
    override suspend fun create(entity: Entity): Long {
        val id = database.query {
            table.insertAndGetId {
                setFields(it, entity)
            }
        }
        return id.value
    }

    @KtorExperimentalAPI
    override suspend fun findByIds(ids: Iterable<Long>): List<Entity> = database.query {
        table.select { table.id inList ids }.mapAll()
    }

    @KtorExperimentalAPI
    override suspend fun delete(id: Long): Unit = database.query {
        table.deleteWhere { table.id eq id }
        Unit
    }

    @KtorExperimentalAPI
    override suspend fun deleteMany(ids: Iterable<Long>): Unit = database.query {
        table.deleteWhere { table.id inList ids }
        Unit
    }

    //    @KtorExperimentalAPI
//    override suspend fun createBatch(entities: Iterable<Entity>): List<Long> = database.query {
//        entities.map { entity ->
//            table.insertAndGetId {
//                setFields(it, entity)
//                it[created] = Instant.now()
//                it[updated] = Instant.now()
//            }.value
//        }
//    }

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

