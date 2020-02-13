package com.lifars.ioc.server.database.tables.sql.auxiliary

import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.ProbeReports
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object FoundIocs : LongIdTable("FoundIocs") {
    val probeReport = reference(
        "probeReport",
        ProbeReports, onDelete = ReferenceOption.CASCADE
    )
    val ioc = reference(
        "ioc",
        Iocs, onDelete = ReferenceOption.CASCADE
    )
}