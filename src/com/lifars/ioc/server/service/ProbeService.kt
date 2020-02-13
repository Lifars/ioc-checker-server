package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.ProbeWithApiKey
import com.lifars.ioc.server.database.repository.CrudRepository
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.exceptions.UserNotFoundException
import com.lifars.ioc.server.payload.Payload
import com.lifars.ioc.server.payload.ProbePayload
import com.lifars.ioc.server.security.PasswordHasher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.time.Instant
import java.time.ZoneOffset

class ProbeService(
    override val repository: ProbeRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
): CrudService<ProbeWithApiKey, ProbePayload.Probe, ProbePayload.SaveProbeWithApiKey> {

    override fun ProbeWithApiKey.toDto() = ProbePayload.Probe (
        id = id,
        name = name,
        created = created,
        updated = updated,
        registeredBy = registeredBy,
        expires = expires,
        userId = owner
    )

    override fun ProbeWithApiKey.toSavedDto(): ProbePayload.SaveProbeWithApiKey = ProbePayload.SaveProbeWithApiKey(
        id = this.id,
        expires = this.expires.atOffset(ZoneOffset.MAX).toLocalDate(),
        name = this.name,
        userId = this.owner,
        apiKeyPlain = "REPLACE_THIS",
        registeredBy = this.registeredBy
    )

    override fun ProbePayload.SaveProbeWithApiKey.toSaveEntity(): ProbeWithApiKey = ProbeWithApiKey(
        id = this.id ?: 0,
        expires = this.expires.atTime(23,59,59).toInstant(ZoneOffset.MAX),
        created = Instant.now(),
        updated = Instant.now(),
        name = this.name,
        owner = this.userId,
        apiKey = ByteArray(0),
        registeredBy = this.registeredBy ?: 0
    )

    /**
     * Do not forget to replace request.data.registeredBy with the current principal id.
     */
    override suspend fun save(request: Payload.Request.Create<ProbePayload.SaveProbeWithApiKey>): Payload.Response.Create<ProbePayload.SaveProbeWithApiKey> =
        registerProbe(request.data.copy(id = 0)).let { Payload.Response.Create(data = it) }

    override suspend fun save(request: Payload.Request.Update<ProbePayload.SaveProbeWithApiKey>): Payload.Response.Update<ProbePayload.SaveProbeWithApiKey> =
        registerProbe(request.data).let { Payload.Response.Update(data = it) }

    override suspend fun save(request: Payload.Request.UpdateMany<ProbePayload.SaveProbeWithApiKey>): Payload.Response.UpdateMany =
        request.ids
            .asFlow()
            .map { request.data.copy(id = it) }
            .map { registerProbe(it) }
            .map { it.id!! }
            .toList()
            .let {
                Payload.Response.UpdateMany(data = it)
            }


    private suspend fun registerProbe(
        request: ProbePayload.SaveProbeWithApiKey
    ): ProbePayload.SaveProbeWithApiKey {
        val ownerId = request.userId
        userRepository.findById(ownerId) ?: throw UserNotFoundException("User with id $ownerId not found")
        val apiKey = request.apiKeyPlain //UUID.randomUUID().toString()
        val apiKeyHashed = passwordHasher.hash(apiKey)
        val probeToSave = request.toSaveEntity().copy(apiKey = apiKeyHashed)
        val savedProbe = repository.save(
            probeToSave
        )
        return savedProbe.toSavedDto().copy(apiKeyPlain = "This is secret")
    }
}