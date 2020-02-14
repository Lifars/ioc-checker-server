package com.lifars.ioc.server.database.tables.sql

import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.libraryextensions.exposed.jsonb

object Iocs : BaseTable("Iocs") {
    val name = varchar("name", 255)
    val definition = jsonb("definition", IocEntry::class)
}