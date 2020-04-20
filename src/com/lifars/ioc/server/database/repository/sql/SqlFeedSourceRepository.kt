package com.lifars.ioc.server.database.repository.sql

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.entities.FeedSource
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.tables.sql.FeedSources
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class SqlFeedSourceRepository(
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

    override suspend fun findOwned(
        pagination: Pagination,
        filter: Filter?,
        sort: Sort?,
        reference: Reference?,
        ownerId: Long
    ): Page<FeedSource> =
        find(pagination, filter, sort, reference)

    override suspend fun findByIdsAndOwner(ids: Iterable<Long>, ownerId: Long): List<FeedSource> =
        findByIds(ids)

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): FeedSource? =
        findById(id)
}