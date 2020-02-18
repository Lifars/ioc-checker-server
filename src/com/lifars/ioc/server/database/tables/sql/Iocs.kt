package com.lifars.ioc.server.database.tables.sql

import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.libraryextensions.exposed.json

object Iocs : BaseTable("Iocs") {
    val name = varchar("name", 255)
    val definition = json("definition", IocEntry::class)
}