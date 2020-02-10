package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.IocSearchError

interface ProbeErrorRepository: CrdRepository<IocSearchError>{
        suspend fun findByProbe(
        probeId: Long,
        pagination: Pagination = Pagination.default
    ): Page<IocSearchError>
}