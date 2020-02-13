package com.lifars.ioc.server.database.entities

import java.time.Instant

data class VisitedFeed(
    val url: String,
    val lastDate: Instant
)