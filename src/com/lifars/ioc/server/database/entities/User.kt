package com.lifars.ioc.server.database.entities

import java.time.Instant

data class User (
    val id: Long,
    val email: String,
    val created: Instant,
    val updated: Instant,
    val role: Role
) {
    enum class Role {
        IOC_CHECKER,
        SMURF_ADMIN
    }
}