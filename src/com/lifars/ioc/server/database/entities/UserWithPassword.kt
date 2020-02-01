package com.lifars.ioc.server.database.entities

import java.time.Instant

data class UserWithPassword (
    val id: Long,
    val name: String,
    val username: String,
    val email: String,
    val company: String,
    val expires: Instant?,
    val created: Instant,
    val updated: Instant,
    val role: Role,
    val password: ByteArray
) {
    enum class Role {
        STANDARD,
        ADMIN
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserWithPassword

        if (id != other.id) return false
        if (name != other.name) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (company != other.company) return false
        if (expires != other.expires) return false
        if (created != other.created) return false
        if (updated != other.updated) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + company.hashCode()
        result = 31 * result + (expires?.hashCode() ?: 0)
        result = 31 * result + created.hashCode()
        result = 31 * result + updated.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}