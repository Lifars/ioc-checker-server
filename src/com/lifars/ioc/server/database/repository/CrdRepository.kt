package com.lifars.ioc.server.database.repository

interface CrdRepository<Entity> {
    suspend fun findById(id: Long): Entity?
    suspend fun create(entity: Entity): Long
    suspend fun save(entity: Entity, entityId: Long? = null): Entity =
        create(entity).let { findById(it)!! }

    suspend fun find(
        pagination: Pagination,
        filter: Filter? = null,
        sort: Sort? = null,
        reference: Reference? = null
    ): Page<Entity>

    suspend fun findByIds(ids: Iterable<Long>): List<Entity>
    suspend fun delete(id: Long)
    suspend fun deleteMany(ids: Iterable<Long>)
}