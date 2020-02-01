package com.lifars.ioc.server.database.tables

object IocSearchErrors : BaseTable("IocSearchErrors") {
    val probeReportId = reference("probeReportId", ProbeReports)
    val iocId = reference("iocId", Iocs).nullable()
    val kind = varchar("kind", 128)
    val message = varchar("message", 1024)
}