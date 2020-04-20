package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.FoundIoc
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.FoundIocs
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.ProbeReports
import com.lifars.ioc.server.database.tables.sql.Probes
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlFoundIocRepository(
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

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: Long
    ): Page<FoundIoc> = database.query {
        table
            .innerJoin(ProbeReports)
            .innerJoin(Probes)
            .slice(table.columns)
            .select(filter, reference) {
                Probes.owner eq ownerId
            }.orderBy(table, sort).mapPaged(pagination)
    }

    override suspend fun findByIdsAndOwner(ids: Iterable<Long>, ownerId: Long): List<FoundIoc> = database.query {
        table
            .innerJoin(ProbeReports)
            .innerJoin(Probes)
            .slice(table.columns)
            .select {
                (Probes.owner eq ownerId) and table.id.inList(ids)
            }.mapAll()
    }

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): FoundIoc?= database.query {
        table
            .innerJoin(ProbeReports)
            .innerJoin(Probes)
            .slice(table.columns)
            .select {
                (table.id eq id) and (Probes.owner eq ownerId)
            }.mapSingle()
    }
}
