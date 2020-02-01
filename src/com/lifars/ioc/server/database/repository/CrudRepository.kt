package com.lifars.ioc.server.database.repository

interface CrudRepository<Entity>: CrdRepository<Entity> {
    suspend fun update(entity: Entity, entityId: Long)

    suspend fun updateMany(entity: Entity, entityIds: Iterable<Long>)

    override suspend fun save(entity: Entity, entityId: Long?): Entity =
        if (entityId != null && entityId > 0) {
            update(entity, entityId)
            findById(entityId)!!
        } else {
            create(entity).let { findById(it)!! }
        }
}