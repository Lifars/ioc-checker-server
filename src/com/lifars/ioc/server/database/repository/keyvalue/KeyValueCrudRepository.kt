package com.lifars.ioc.server.database.repository.keyvalue

import com.lifars.ioc.server.database.repository.CrudRepository

interface KeyValueCrudRepository<Entity, K, V> :
    KeyValueCrdRepository<Entity, K, V>,
    CrudRepository<K, Entity> {

    /**
     * @param entityId is ignored
     */
    override suspend fun update(entity: Entity, entityId: K) {
        save(entity)
    }

    /**
     * @param entityIds is ignored
     */
    override suspend fun updateMany(entity: Entity, entityIds: Iterable<K>) {
        update(entity, entity.databaseKey())
    }

    override suspend fun save(entity: Entity, entityId: K?): Entity =
        super<KeyValueCrdRepository>.save(entity, entityId)
}