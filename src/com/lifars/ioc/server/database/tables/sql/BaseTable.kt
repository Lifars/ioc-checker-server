package com.lifars.ioc.server.database.tables.sql

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

abstract class BaseTable(name: String) : LongIdTable(name, "_id") {
    val created = timestamp("created").clientDefault { Instant.now() }
    val updated = timestamp("updated").clientDefault { Instant.now() }
}