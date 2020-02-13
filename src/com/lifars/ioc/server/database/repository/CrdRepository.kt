package com.lifars.ioc.server.database.repository

interface CrdRepository<Id, Entity> {
    suspend fun findById(id: Id): Entity?
    suspend fun create(entity: Entity): Id
    suspend fun save(entity: Entity, entityId: Id? = null): Entity =
        create(entity).let { findById(it)!! }

    suspend fun find(
        pagination: Pagination,
        filter: Filter? = null,
        sort: Sort? = null,
        reference: Reference? = null
    ): Page<Entity>

    suspend fun findAll(
    ): List<Entity> =
        find(
            pagination = Pagination(
                offset = 0,
                limit = Int.MAX_VALUE
            )
        )

    suspend fun findByIds(ids: Iterable<Id>): List<Entity>
    suspend fun delete(id: Id)
    suspend fun deleteMany(ids: Iterable<Id>)
}