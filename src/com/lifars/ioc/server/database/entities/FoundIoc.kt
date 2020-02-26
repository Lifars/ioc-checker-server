package com.lifars.ioc.server.database.entities

import java.time.Instant

data class FoundIoc(
    val id: Long,
    val probeReportId: Long,
    val iocId: Long,
    val created: Instant,
    val updated: Instant
)