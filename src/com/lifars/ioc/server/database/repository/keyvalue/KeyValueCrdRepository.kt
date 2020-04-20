package com.lifars.ioc.server.database.repository.keyvalue

import com.lifars.ioc.server.database.ChronicleMapDatabase
import com.lifars.ioc.server.database.repository.*

interface KeyValueCrdRepository<Entity, K, V> : CrdRepository<K, Entity> {
    val storage: ChronicleMapDatabase<K, V>

    override suspend fun findById(id: K): Entity? =
        storage[id]?.let { toEntity(id, it) }

    override suspend fun create(entity: Entity): K {
        val key = entity.databaseKey()
        storage[key] = entity.toDatabaseValue()
        return key
    }

    /**
     * @param entityId is ignored
     */
    override suspend fun save(entity: Entity, entityId: K?): Entity {
        create(entity)
        return entity
    }

    /**
     * Warn. This is key-value database storage.
     * This function is very inefficient for large databases.
     * @param filter is ignored
     * @param sort is ignored
     * @param reference is ignored
     */
    override suspend fun find(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?
    ): Page<Entity> =
        storage.entries
            .toList()
            .subList(pagination.offset, pagination.offset + pagination.limit)
            .map { toEntity(it.key, it.value) }
            .asPage(
                totalSize = storage.size,
                offset = pagination.offset
            )

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: K
    ): Page<Entity> =
        storage.entries
            .toList()
            .filter { it.value.ownerId() == ownerId }
            .subList(pagination.offset, pagination.offset + pagination.limit)
            .map { toEntity(it.key, it.value) }
            .asPage(
                totalSize = storage.size,
                offset = pagination.offset
            )

    fun V.ownerId(): K

    override suspend fun findByIdAndOwner(id: K, ownerId: K): Entity? =
        storage[id]?.let { candidate ->
            if (candidate.ownerId() == ownerId) {
                toEntity(id, candidate)
            } else {
                null
            }
        }


    override suspend fun findByIdsAndOwner(ids: Iterable<K>, ownerId: Long): List<Entity> =
        storage
            .filterKeys { it in ids }
            .filterValues { it.ownerId() == ownerId }
            .map { toEntity(it.key, it.value) }

    override suspend fun findByIds(ids: Iterable<K>): List<Entity> =
        storage
            .filterKeys { it in ids }
            .map { toEntity(it.key, it.value) }

    override suspend fun delete(id: K) {
        storage.remove(id)
    }

    override suspend fun deleteMany(ids: Iterable<K>) {
        ids.forEach { delete(it) }
    }

    fun toEntity(key: K, value: V): Entity

    fun Entity.toDatabaseValue(): V

    fun Entity.databaseKey(): K
}