package com.lifars.ioc.server.payload

import java.time.Instant
import java.time.LocalDate

object ProbePayload {

    object Request {
        data class Create(
            override val data: SaveProbeWithApiKey
        ): Payload.Request.Create<SaveProbeWithApiKey>

        data class Update(
            override val id: Long,
            override val data: SaveProbeWithApiKey,
            override val previousData: SaveProbeWithApiKey
        ): Payload.Request.Update<SaveProbeWithApiKey>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: SaveProbeWithApiKey
        ): Payload.Request.UpdateMany<SaveProbeWithApiKey>
    }

    data class Probe(
        val id: Long,
        val name: String,
        val expires: Instant,
        val userId: Long,
        val created: Instant,
        val updated: Instant,
        val registeredBy: Long
    )

    data class SaveProbeWithApiKey(
        val id: Long?,
        val name: String,
        val expires: LocalDate,
        val userId: Long,
        val apiKeyPlain: String,
        val registeredBy: Long?
    )

        data class IocSearchResult(
        val iocId: Long,
        val data: List<String>
    )

    data class IocSearchError(
        val iocId: Long?,
        val kind: String,
        val message: String
    )
}

