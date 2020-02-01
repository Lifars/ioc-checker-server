package com.lifars.ioc.server.database.entities

data class IocSearchResult(
    val iocId: Long,
    val probeReportId: Long,
    val data: List<String>
)