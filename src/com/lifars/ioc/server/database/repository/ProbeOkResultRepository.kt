package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.IocSearchResult

interface ProbeOkResultRepository : CrdRepository<IocSearchResult> {
    suspend fun findByIocReport(
        iocId: Long,
        pagination: Pagination = Pagination.default
    ): Page<IocSearchResult>

    suspend fun findByProbe(
        probeId: Long,
        pagination: Pagination = Pagination.default
    ): Page<IocSearchResult>

    suspend fun findByProbeAndIoc(
        probeId: Long,
        iocId: Long,
        pagination: Pagination = Pagination.default
    ): Page<IocSearchResult>
}