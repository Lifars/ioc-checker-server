package com.lifars.ioc.server.payload

object FoundIocPayload {

    object Request {
        data class Create(
            override val data: FoundIoc
        ) : Payload.Request.Create<FoundIoc>

        data class Update(
            override val id: Long,
            override val data: FoundIoc,
            override val previousData: FoundIoc
        ) : Payload.Request.Update<FoundIoc>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: FoundIoc
        ) : Payload.Request.UpdateMany<FoundIoc>
    }

    data class FoundIoc(
        val id: Long,
        val probeReportId: Long,
        val iocId: Long
    )
}