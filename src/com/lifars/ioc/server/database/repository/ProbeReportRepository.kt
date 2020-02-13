package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.ProbeReport
import java.time.Instant

interface ProbeReportRepository : CrudRepository<Long, ProbeReport> {
    suspend fun findByIoc(
        iocId: Long,
        timeInterval: Interval<Instant>? = null,
        pagination: Pagination
    ): Page<ProbeReport>

    suspend fun findByProbe(
        probeId: Long,
        timeInterval: Interval<Instant>? = null,
        pagination: Pagination
    ): Page<ProbeReport>

    suspend fun findByProbeAndIoc(
        probeId: Long,
        iocId: Long,
        timeInterval: Interval<Instant>? = null,
        pagination: Pagination): Page<ProbeReport>
}