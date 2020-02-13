package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.FeedSource
import com.lifars.ioc.server.database.repository.FeedSourceRepository
import com.lifars.ioc.server.database.tables.sql.FeedSources
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlFeedSourceRepository @KtorExperimentalAPI constructor(
    override val database: Database
): FeedSourceRepository, SqlCrudRepository<FeedSource, FeedSources> {
    override val table: FeedSources
        get() = FeedSources

    override suspend fun ResultRow.toEntity() = FeedSource(
        id = this[FeedSources.id].value,
        url = this[FeedSources.url],
        type = this[FeedSources.type].name.let { FeedSource.FeedType.valueOf(it) }
    )

    override fun FeedSources.setFields(row: UpdateBuilder<Number>, entity: FeedSource) {
        row[url] = entity.url
        row[type] = entity.type.name.let { FeedSources.FeedType.valueOf(it) }
    }
}