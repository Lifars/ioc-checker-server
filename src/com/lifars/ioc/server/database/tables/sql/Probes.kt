package com.lifars.ioc.server.database.tables.sql

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Duration
import java.time.Instant

object Probes : BaseTable("Probes") {
    val name = varchar("name", 255)
    val apiKey = binary("apiKey", 60)
    val expires = timestamp("expires").default(Instant.now() + Duration.ofDays(365 * 50L)) // Cannot add Period.ofYears to timestamp directly
    val owner = reference("owner", Users, onDelete = ReferenceOption.CASCADE)
    val registeredBy = reference("registeredBy", Users)

    init {
        index(false,
            name,
            apiKey
        )
    }
}