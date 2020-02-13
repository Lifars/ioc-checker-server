package com.lifars.ioc.server.database.tables.sql

object IocSearchResults: BaseTable("IocSearchResults"){
    val probeReportId = reference("probeReportId",
        ProbeReports
    )
    val iocId = reference("iocId", Iocs)
    val data = text("data")
}