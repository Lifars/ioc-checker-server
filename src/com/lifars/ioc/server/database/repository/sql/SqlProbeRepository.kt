package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.ProbeApiKey
import com.lifars.ioc.server.database.entities.ProbeWithApiKey
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.database.tables.sql.Probes
import com.lifars.ioc.server.database.tables.sql.Users
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.Instant

class SqlProbeRepository @KtorExperimentalAPI constructor(
    override val database: Database
) : SqlCrudRepository<ProbeWithApiKey, Probes>, ProbeRepository {

    override suspend fun containsByNameAndApiKey(name: String, apiKey: ByteArray): Boolean = database.query {
        Probes
            .slice(Probes.name, Probes.apiKey)
            .select { (Probes.name eq name) and (Probes.apiKey eq apiKey) }
            .limit(1)
            .count() > 0
    }

    override suspend fun findByName(name: String): ProbeWithApiKey? = database.query {
        Probes
            .select { Probes.name eq name }
            .limit(1)
            .firstOrNull()
            ?.let { it.toEntity() }
    }

    override suspend fun findIdAndApiKeyByName(name: String): ProbeApiKey? = database.query {
        Probes
            .slice(Probes.id, Probes.apiKey)
            .select { (Probes.name eq name) and (Probes.expires greaterEq Instant.now()) }
            .limit(1)
            .firstOrNull()
            ?.let {
                ProbeApiKey(
                    probeId = it[Probes.id].value,
                    apikey = it[Probes.apiKey]
                )
            }
    }

    override suspend fun ResultRow.toEntity(): ProbeWithApiKey = toProbe()

    override fun Probes.setFields(row: UpdateBuilder<Number>, entity: ProbeWithApiKey) {
        row[expires] = entity.expires
        row[name] = entity.name
        row[owner] = EntityID(entity.owner, Users)
        row[apiKey] = entity.apiKey
        row[registeredBy] = EntityID(entity.registeredBy,
            Users
        )
    }

    override val table: Probes
        get() = Probes

}

fun ResultRow.toProbe() = ProbeWithApiKey(
    id = this[Probes.id].value,
    name = this[Probes.name],
    expires = this[Probes.expires],
    owner = this[Probes.owner].value,
    updated = this[Probes.updated],
    created = this[Probes.created],
    apiKey = this[Probes.apiKey],
    registeredBy = this[Probes.registeredBy].value
)