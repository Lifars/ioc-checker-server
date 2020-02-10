package com.lifars.ioc.server.database.tables

object IocSearchErrors : BaseTable("IocSearchErrors") {
    val probeReportId = reference("probeReportId", ProbeReports)
    val kind = varchar("kind", 128)
    val message = varchar("message", 1024)
}