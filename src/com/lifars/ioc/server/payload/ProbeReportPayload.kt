package com.lifars.ioc.server.payload

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

object ProbeReportPayload {

    object Request {
        data class ReportUpload(
            val datetime: Instant = Instant.now(),
            val foundIocs: List<Long>,
            val iocResults: List<IocSearchResult>,
            val iocErrors: List<IocSearchError>
        )

        data class Create(
            override val data: SaveProbeReport
        ) : Payload.Request.Create<SaveProbeReport>

        data class Update(
            override val id: Long,
            override val data: SaveProbeReport,
            override val previousData: SaveProbeReport
        ) : Payload.Request.Update<SaveProbeReport>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: SaveProbeReport
        ) : Payload.Request.UpdateMany<SaveProbeReport>
    }

    data class IocSearchResult(
        val iocId: Long,
        val data: List<String>
    )

    data class IocSearchError(
        val iocId: Long?,
        val kind: String,
        val message: String
    )

    data class ProbeReport(
        val id: Long,
        val probeId: Long,
        val foundIocs: List<Long>,
        val iocResults: List<IocSearchResult>,
        val iocErrors: List<IocSearchError>,
        val created: Instant,
        val updated: Instant,
        val probeTimestamp: Instant
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SaveProbeReport(
        val id: Long,
        val probeId: Long,
        val foundIocs: List<Long>,
        val iocResults: List<IocSearchResult>,
        val iocErrors: List<IocSearchError>,
        val probeTimestamp: Instant
    )
}