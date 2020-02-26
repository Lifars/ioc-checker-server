package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.FoundIoc
import com.lifars.ioc.server.database.repository.FoundIocRepository
import com.lifars.ioc.server.database.tables.sql.FoundIocs
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.ProbeReports
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlFoundIoctRepository(
    override val database: Database
) : FoundIocRepository, SqlCrudRepository<FoundIoc, FoundIocs> {

    override val table: FoundIocs
        get() = FoundIocs

    override suspend fun ResultRow.toEntity() = FoundIoc(
        probeReportId = this[table.probeReportId].value,
        iocId = this[table.iocId].value,
        created = this[table.created],
        updated = this[table.updated],
        id = this[table.id].value
    )

    override fun FoundIocs.setFields(
        row: UpdateBuilder<Number>,
        entity: FoundIoc
    ) {
        row[probeReportId] = EntityID(
            entity.probeReportId,
            ProbeReports
        )
        row[iocId] = EntityID(
            entity.iocId,
            Iocs
        )
    }
}
