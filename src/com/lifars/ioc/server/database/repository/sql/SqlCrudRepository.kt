package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.repository.CrudRepository
import com.lifars.ioc.server.database.tables.sql.BaseTable
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.update
import java.time.Instant

interface SqlCrudRepository<Entity, Table : BaseTable>
    : CrudRepository<Long, Entity>, SqlCrdRepository<Entity, Table> {

    override suspend fun updateMany(entity: Entity, entityIds: Iterable<Long>): Unit = database.query {
        table.update({ table.id inList entityIds }) { row ->
            setFields(row, entity)
            row[updated] = Instant.now()
        }
        Unit
    }

    override suspend fun update(entity: Entity, entityId: Long): Unit = database.query {
        table.update({ table.id eq entityId }) { row ->
            setFields(row, entity)
            row[updated] = Instant.now()
        }
        Unit
    }
}

