package com.lifars.ioc.server.payload

object FeedSourcePayload {
    object Request {
        data class Create(
            override val data: FeedSource
        ) : Payload.Request.Create<FeedSource>

        data class Update(
            override val id: Long,
            override val data: FeedSource,
            override val previousData: FeedSource
        ) : Payload.Request.Update<FeedSource>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: FeedSource
        ) : Payload.Request.UpdateMany<FeedSource>
    }

    data class FeedSource(
        val id: Long,
        val url: String,
        val type: FeedType
    )

    enum class FeedType {
        MISP,
        AUTO_DETECT
    }
}