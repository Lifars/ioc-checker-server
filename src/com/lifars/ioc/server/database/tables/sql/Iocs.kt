package com.lifars.ioc.server.database.tables.sql

import com.lifars.ioc.server.database.tables.sql.BaseTable

object Iocs : BaseTable("Iocs") {
    val name = varchar("name", 255)
    val definition = text("definition")
}