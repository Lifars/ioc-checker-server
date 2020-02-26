package com.lifars.ioc.server.database.entities

import java.time.Instant

data class ProbeReport(
    val id: Long,
    val probeId: Long,
    val foundIocs: List<Long>,
    val created: Instant,
    val updated: Instant,
    val probeTimestamp: Instant
)