package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.IocSearchResults
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.auxiliary.FoundIocs
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.Instant

class SqlIocRepository @KtorExperimentalAPI constructor(override val database: Database) :
    IocRepository,
    SqlCrudRepository<Ioc, Iocs> {

    override val table: Iocs
        get() = Iocs

    override suspend fun ResultRow.toEntity(): Ioc = toIoc()

    override fun Iocs.setFields(row: UpdateBuilder<Number>, entity: Ioc) {
        row[name] = entity.name
        row[definition] = entity.definition
    }

    override suspend fun findBetween(
        interval: Interval<Instant>,
        pagination: Pagination
    ): Page<Ioc> = database.query {
        table.select {
            table.updated between interval
        }.mapPaged(pagination)
    }

    override suspend fun findNewerThan(instant: Instant): List<Ioc> = database.query {
        table.select {
            table.updated greaterEq instant
        }.map { it.toEntity() }
    }

    override suspend fun findByProbeReport(probeResultId: Long): List<Ioc> = database.query {
        (table innerJoin IocSearchResults)
            .slice(table.columns)
            .select { IocSearchResults.probeReportId eq probeResultId }
            .mapAll()
    }

    override suspend fun findIdsByProbeReport(probeResultId: Long): List<Long> = database.query {
        (table innerJoin FoundIocs)
            .slice(table.id)
            .select { FoundIocs.probeReport eq probeResultId }
            .map{ it[table.id].value }
    }
}

fun ResultRow.toIoc() = Ioc(
    id = this[Iocs.id].value,
    name = this[Iocs.name],
    definition = this[Iocs.definition],
    created = this[Iocs.created],
    updated = this[Iocs.updated]
)