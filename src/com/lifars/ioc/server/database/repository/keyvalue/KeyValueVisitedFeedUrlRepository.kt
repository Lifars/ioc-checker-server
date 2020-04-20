package com.lifars.ioc.server.database.repository.keyvalue

import com.lifars.ioc.server.database.entities.VisitedFeed
import com.lifars.ioc.server.database.repository.VisitedFeedUrlRepository
import com.lifars.ioc.server.database.tables.keyvalue.VisitedFeeds
import java.time.Instant

class KeyValueVisitedFeedUrlRepository(
    override val storage: VisitedFeeds
) : VisitedFeedUrlRepository, KeyValueCrudRepository<VisitedFeed, String, String> {
    override fun toEntity(key: String, value: String) = VisitedFeed(
        url = key,
        lastDate = Instant.ofEpochMilli(value.toLong())
    )

    override fun VisitedFeed.toDatabaseValue() = lastDate.toEpochMilli().toString()

    override fun VisitedFeed.databaseKey() = url

    override fun String.ownerId(): String = this
}