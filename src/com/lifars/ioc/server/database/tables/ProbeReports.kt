package com.lifars.ioc.server.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp

object ProbeReports : BaseTable("ProbeReports") {
    val probeId = reference("probeId", Probes, onDelete = ReferenceOption.CASCADE)
    val probeTimestamp = timestamp("probeTimestamp")
}

