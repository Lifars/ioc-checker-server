package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.ProbeReport
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.*
import com.lifars.ioc.server.database.tables.sql.auxiliary.FoundIocs
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.Instant

class SqlProbeReportRepository(
    override val database: Database,
    private val iocRepository: IocRepository,
    private val probeOkRepository: ProbeOkResultRepository,
    private val probeErrorRepository: ProbeErrorRepository
) : ProbeReportRepository, SqlCrudRepository<ProbeReport, ProbeReports> {

    override suspend fun findByProbe(
        probeId: Long,
        timeInterval: Interval<Instant>?,
        pagination: Pagination
    ): Page<ProbeReport> = database.query {
        table.select {
            (table.probeId eq EntityID(probeId, Probes)) andMaybe
                    (table.updated maybeBetween timeInterval)
        }.mapPaged(pagination)
    }

    override suspend fun findByProbeAndIoc(
        probeId: Long,
        iocId: Long,
        timeInterval: Interval<Instant>?,
        pagination: Pagination
    ): Page<ProbeReport> = database.query {
        (table innerJoin IocSearchResults).select {
            (table.probeId eq EntityID(probeId, Probes)) and
                    (IocSearchResults.iocId eq EntityID(iocId,
                        Iocs
                    )) andMaybe
                    (table.updated maybeBetween timeInterval)
        }.mapPaged(pagination)
    }

    override suspend fun findByIoc(
        iocId: Long,
        timeInterval: Interval<Instant>?,
        pagination: Pagination
    ): Page<ProbeReport> = database.query {
        (table innerJoin IocSearchResults).select {
            (IocSearchResults.iocId eq EntityID(iocId,
                Iocs
            )) andMaybe
                    (table.updated maybeBetween timeInterval)
        }.mapPaged(pagination)
    }

    override val table: ProbeReports
        get() = ProbeReports

    override suspend fun create(entity: ProbeReport): Long {
        val createdId = super.create(entity)
        val results = entity.iocResults
        val createdIdWrapped = EntityID(createdId,
            ProbeReports
        )
        database.query {
            IocSearchResults.batchInsert(results) { searchResult ->
                this[IocSearchResults.probeReportId] = createdIdWrapped
                this[IocSearchResults.data] = searchResult.data.joinToString("\n")
                this[IocSearchResults.iocId] = EntityID(searchResult.iocId,
                    Iocs
                )
            }

            val errors = entity.iocErrors
            IocSearchErrors.batchInsert(errors) { searchError ->
                this[IocSearchErrors.probeReportId] = createdIdWrapped
                this[IocSearchErrors.kind] = searchError.kind
                this[IocSearchErrors.message] = searchError.message
            }

            FoundIocs.batchInsert(entity.foundIocs) { foundIocId ->
                this[FoundIocs.ioc] = EntityID(foundIocId,
                    Iocs
                )
                this[FoundIocs.probeReport] = createdIdWrapped
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
        iocErrors = probeErrorRepository.findByProbe(this[table.probeId].value).data,
        iocResults = probeOkRepository.findByProbe(this[table.probeId].value).data,
        id = this[table.id].value
    )

    override fun ProbeReports.setFields(
        row: UpdateBuilder<Number>,
        entity: ProbeReport
    ) {
        row[probeId] = EntityID(entity.probeId,
            ProbeReports
        )
        row[probeTimestamp] = entity.probeTimestamp
    }
}
