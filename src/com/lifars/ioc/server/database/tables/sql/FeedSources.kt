package com.lifars.ioc.server.database.tables.sql

object FeedSources : BaseTable("FeedSources") {
    val url = varchar("url", 512)
    val type = enumeration("type", FeedType::class)

    enum class FeedType{
        AUTO_DETECT,
        MISP
    }
}
