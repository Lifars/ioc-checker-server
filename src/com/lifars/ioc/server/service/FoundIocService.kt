package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.FoundIoc
import com.lifars.ioc.server.database.repository.FoundIocRepository
import com.lifars.ioc.server.payload.FoundIocPayload
import java.time.Instant

class FoundIocService(
    override val repository: FoundIocRepository
) : CrudService<FoundIoc, FoundIocPayload.FoundIoc, FoundIocPayload.FoundIoc> {
    override fun FoundIoc.toDto() = FoundIocPayload.FoundIoc(
        id = id,
        probeReportId = probeReportId,
        iocId = iocId
    )

    override fun FoundIoc.toSavedDto() = toDto()

    override fun FoundIocPayload.FoundIoc.toSaveEntity() = FoundIoc(
        id = id,
        probeReportId = probeReportId,
        iocId = iocId,
        updated = Instant.now(),
        created = Instant.now()
    )
}