package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.ProbeWithApiKey
import com.lifars.ioc.server.database.entities.ProbeApiKey

interface ProbeRepository : CrudRepository<ProbeWithApiKey> {
    suspend fun containsByNameAndApiKey(name: String, apiKey: ByteArray): Boolean
    suspend fun findByName(name: String): ProbeWithApiKey?
    suspend fun findIdAndApiKeyByName(name: String): ProbeApiKey?
}
