package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.ProbeReport
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.FoundIocs
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.ProbeReports
import com.lifars.ioc.server.database.tables.sql.Probes
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlProbeReportRepository(
    override val database: Database,
    private val iocRepository: IocRepository
) : ProbeReportRepository, SqlCrudRepository<ProbeReport, ProbeReports> {

    override val table: ProbeReports
        get() = ProbeReports

    override suspend fun create(entity: ProbeReport): Long {
        val createdId = super.create(entity)
        val createdIdWrapped = EntityID(
            createdId,
            ProbeReports
        )
        database.query {
            FoundIocs.batchInsert(entity.foundIocs) { foundIocId ->
                this[FoundIocs.iocId] = EntityID(
                    foundIocId,
                    Iocs
                )
                this[FoundIocs.probeReportId] = createdIdWrapped
            }
        }
        return createdId
    }

    override suspend fun ResultRow.toEntity() = ProbeReport(
        probeId = this[table.probeId].value,
        probeTimestamp = this[table.probeTimestamp],
        created = this[table.created],
        updated = this[table.updated],
        foundIocs = iocRepository.findIdsByProbeReport(this[table.id].value),
        id = this[table.id].value
    )

    override fun ProbeReports.setFields(
        row: UpdateBuilder<Number>,
        entity: ProbeReport
    ) {
        row[probeId] = EntityID(
            entity.probeId,
            ProbeReports
        )
        row[probeTimestamp] = entity.probeTimestamp
    }

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: Long
    ): Page<ProbeReport> = database.query {
        table
            .innerJoin(Probes)
            .slice(table.columns)
            .select(filter, reference) {
                Probes.owner eq ownerId
            }.orderBy(table, sort).mapPaged(pagination)
    }

    override suspend fun findByIdsAndOwner(ids: Iterable<Long>, ownerId: Long): List<ProbeReport> = database.query {
        table
            .innerJoin(Probes)
            .slice(table.columns)
            .select {
                (Probes.owner eq ownerId) and table.id.inList(ids)
            }.mapAll()
    }

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): ProbeReport? = database.query {
        table
            .innerJoin(Probes)
            .slice(table.columns)
            .select {
                (table.id eq ownerId) and (Probes.owner eq ownerId)
            }.mapSingle()
    }
}
