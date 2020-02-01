package com.lifars.ioc.server.database.entities

data class IocSearchError(
    val iocId: Long?,
    val probeReportId: Long,
    val kind: String,
    val message: String
)