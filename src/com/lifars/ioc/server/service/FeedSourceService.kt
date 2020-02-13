package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.FeedSource
import com.lifars.ioc.server.database.repository.FeedSourceRepository
import com.lifars.ioc.server.payload.FeedSourcePayload

class FeedSourceService(
    override val repository: FeedSourceRepository
) : CrudService<FeedSource, FeedSourcePayload.FeedSource, FeedSourcePayload.FeedSource> {

    override fun FeedSource.toDto() = FeedSourcePayload.FeedSource(
        id = id,
        url = url,
        type = FeedSourcePayload.FeedType.valueOf(type.name)
    )

    override fun FeedSource.toSavedDto() = toDto()

    override fun FeedSourcePayload.FeedSource.toSaveEntity() = FeedSource(
        id = id,
        url = url,
        type = FeedSource.FeedType.valueOf(type.name)
    )
}