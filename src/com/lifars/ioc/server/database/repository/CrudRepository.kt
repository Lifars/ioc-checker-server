package com.lifars.ioc.server.database.repository

interface CrudRepository<Id, Entity>: CrdRepository<Id,  Entity> {
    suspend fun update(entity: Entity, entityId: Id)

    suspend fun updateMany(entity: Entity, entityIds: Iterable<Id>)

    override suspend fun save(entity: Entity, entityId: Id?): Entity =
        if (entityId != null) {
            update(entity, entityId)
            findById(entityId)!!
        } else {
            create(entity).let { findById(it)!! }
        }
}