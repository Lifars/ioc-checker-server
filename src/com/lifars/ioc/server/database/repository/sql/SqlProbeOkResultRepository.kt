package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.IocSearchResult
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.IocSearchResults
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.ProbeReports
import com.lifars.ioc.server.database.tables.sql.Probes
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlProbeOkResultRepository(override val database: Database) :
    ProbeOkResultRepository,
    SqlCrdRepository<IocSearchResult, IocSearchResults> {

    override suspend fun findByIocReport(
        iocId: Long,
        pagination: Pagination
    ): Page<IocSearchResult> = database.query {
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
    ): Page<IocSearchResult> = database.query {
        val query = (table innerJoin ProbeReports)
            .slice(table.columns)
            .select {
                ProbeReports.probeId eq EntityID(probeId,
                    Probes
                )
            }
        query.limit(pagination)
            .map { it.toEntity() }
            .asPage(query.count(), pagination.offset)
    }

    override suspend fun findByProbeAndIoc(
        probeId: Long,
        iocId: Long,
        pagination: Pagination
    ): Page<IocSearchResult> = database.query {
        val query = (table innerJoin ProbeReports)
            .slice(table.columns)
            .select {
                (table.iocId eq EntityID(iocId, Iocs)) and (ProbeReports.probeId eq EntityID(probeId,
                    Probes
                ))
            }
        query.limit(pagination)
            .map { it.toEntity() }
            .asPage(query.count(), pagination.offset)
    }

    override val table: IocSearchResults
        get() = IocSearchResults

    override suspend fun ResultRow.toEntity() = toIocSearchResult()

    override fun IocSearchResults.setFields(row: UpdateBuilder<Number>, entity: IocSearchResult) {
        row[iocId] = EntityID(entity.iocId, Iocs)
        row[probeReportId] = EntityID(entity.probeReportId,
            ProbeReports
        )
        row[data] = entity.data.joinToString(separator = "\n")
    }
}

fun ResultRow.toIocSearchResult() = IocSearchResult(
    iocId = this[IocSearchResults.iocId]?.value ?: 0,
    probeReportId = this[IocSearchResults.probeReportId].value,
    data = this[IocSearchResults.data].split("\n")
)