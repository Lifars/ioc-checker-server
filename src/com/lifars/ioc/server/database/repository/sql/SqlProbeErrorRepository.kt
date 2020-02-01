package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.IocSearchError
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.IocSearchErrors
import com.lifars.ioc.server.database.tables.Iocs
import com.lifars.ioc.server.database.tables.ProbeReports
import com.lifars.ioc.server.database.tables.Probes
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlProbeErrorRepository @KtorExperimentalAPI constructor(override val database: Database) :
    ProbeErrorRepository,
    SqlCrdRepository<IocSearchError, IocSearchErrors> {

    override suspend fun findByIoc(
        iocId: Long,
        pagination: Pagination
    ): Page<IocSearchError> = database.query {
        val query = table.select {
            table.iocId eq EntityID(iocId, Iocs)
        }
        query.limit(pagination)
            .map { it.toEntity() }
            .asPage(query.count(), pagination.offset)
    }

    override suspend fun findByProbe(
        probeId: Long,
        pagination: Pagination
    ): Page<IocSearchError> = database.query {
        val query = (table innerJoin ProbeReports)
            .slice(table.columns)
            .select {
                ProbeReports.probeId eq EntityID(probeId, Probes)
            }
        query.limit(pagination)
            .map { it.toEntity() }
            .asPage(query.count(), pagination.offset)
    }

    override suspend fun findByProbeAndIoc(
        probeId: Long,
        iocId: Long,
        pagination: Pagination
    ): Page<IocSearchError> = database.query {
        val query = (table innerJoin ProbeReports)
            .slice(table.columns).select {
                (table.iocId eq EntityID(iocId, Iocs)) and (ProbeReports.probeId eq EntityID(probeId, Probes))
            }
        query.limit(pagination)
            .map { it.toEntity() }
            .asPage(query.count(), pagination.offset)
    }

    override val table: IocSearchErrors
        get() = IocSearchErrors

    override suspend fun ResultRow.toEntity() = toIocSearchError()

    override fun IocSearchErrors.setFields(row: UpdateBuilder<Number>, entity: IocSearchError) {
        row[probeReportId] = EntityID(entity.probeReportId, Probes)
        row[iocId] = entity.iocId?.let { EntityID(entity.iocId, Iocs) }
        row[kind] = entity.kind
        row[message] = entity.message
    }
}

fun ResultRow.toIocSearchError() = IocSearchError(
    iocId = this[IocSearchErrors.iocId]?.value ?: 0,
    probeReportId = this[IocSearchErrors.probeReportId].value,
    kind = this[IocSearchErrors.kind],
    message = this[IocSearchErrors.message]
)