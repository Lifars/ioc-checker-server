package com.lifars.ioc.server.database.entities

import java.time.Instant

data class Ioc(
    val id: Long,
    val created: Instant,
    val updated: Instant,
    val name: String,
    val definition: IocEntry
)