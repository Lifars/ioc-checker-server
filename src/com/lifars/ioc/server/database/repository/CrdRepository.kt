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

    /**
     * If [Entity] has no owner, than this function should just call [find]
     */
    suspend fun findOwned(
        pagination: Pagination,
        filter: Filter? = null,
        sort: Sort? = null,
        reference: Reference? = null,
        ownerId: Id
    ): Page<Entity>

    /**
     * If [Entity] has no owner, than this function should just call [findById]
     */
    suspend fun findByIdAndOwner(id: Id, ownerId: Id): Entity?

    suspend fun findAll(
    ): List<Entity> =
        find(
            pagination = Pagination(
                offset = 0,
                limit = Int.MAX_VALUE
            )
        )

    suspend fun findByIds(ids: Iterable<Id>): List<Entity>

    /**
     * If [Entity] has no owner, than this function should just call [findByIds]
     */
    suspend fun findByIdsAndOwner(ids: Iterable<Id>, ownerId: Long): List<Entity>

    suspend fun delete(id: Id)
    suspend fun deleteMany(ids: Iterable<Id>)
}