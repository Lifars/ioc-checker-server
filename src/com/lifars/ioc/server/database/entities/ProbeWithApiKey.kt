package com.lifars.ioc.server.database.entities

import java.time.Instant

data class ProbeWithApiKey (
    val id: Long,
    val name: String,
    val expires: Instant,
    val owner: Long,
    val created: Instant,
    val updated: Instant,
    val apiKey: ByteArray,
    val registeredBy: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProbeWithApiKey

        if (id != other.id) return false
        if (name != other.name) return false
        if (expires != other.expires) return false
        if (owner != other.owner) return false
        if (created != other.created) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + expires.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + updated.hashCode()
        return result
    }
}

data class ProbeApiKey(
    val probeId: Long,
    val apikey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProbeApiKey

        if (probeId != other.probeId) return false

        return true
    }

    override fun hashCode(): Int {
        return probeId.hashCode()
    }
}