package com.lifars.ioc.server.database.entities

data class IocSearchError(
    val probeReportId: Long,
    val kind: String,
    val message: String
)