package com.lifars.ioc.server.database.tables.sql

import org.jetbrains.exposed.sql.ReferenceOption

object FoundIocs : BaseTable("FoundIocs") {
    val probeReportId = reference(
        "probeReportId",
        ProbeReports, onDelete = ReferenceOption.CASCADE
    )
    val iocId = reference(
        "iocId",
        Iocs, onDelete = ReferenceOption.CASCADE
    )
}