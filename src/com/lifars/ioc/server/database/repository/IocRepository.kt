package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.Ioc
import java.time.Instant

interface IocRepository: CrudRepository<Long, Ioc> {
    suspend fun findBetween(
        interval: Interval<Instant>,
        pagination: Pagination
    ): Page<Ioc>

    suspend fun findNewerThan(instant: Instant): List<Ioc>

    suspend fun findIdsByProbeReport(probeResultId: Long): List<Long>
}