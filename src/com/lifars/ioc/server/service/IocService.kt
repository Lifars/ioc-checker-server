package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.database.repository.IocRepository
import com.lifars.ioc.server.payload.IocPayload
import java.time.Duration
import java.time.Instant

class IocService(
    override val repository: IocRepository
) : CrudService<Ioc, IocPayload.Ioc, IocPayload.SaveIoc> {


    private val iocConverter = IocConverter()

    override fun Ioc.toDto(): IocPayload.Ioc = this.toResponseIoc(iocConverter)

    fun IocPayload.Ioc.toEntity() = Ioc(
        id = this.id,
        updated = Instant.now(),
        created = Instant.now(),
        name = this.name,
        definition = iocConverter.iocEntryToPayloadRec(this.definition)
    )

    override fun Ioc.toSavedDto() = toSaveResponseIoc(iocConverter)

    override fun IocPayload.SaveIoc.toSaveEntity() = Ioc(
        id = this.id,
        name = this.name,
        created = Instant.now(),
        updated = Instant.now(),
        definition = iocConverter.iocEntryToPayloadRec(this.definition)
    )

    suspend fun latestIocs(hours: Int): IocPayload.Response.LatestIocs {
        val iocConverter = IocConverter()
        val iocs = repository.findNewerThan(
            instant = Instant.now() - Duration.ofHours(hours.toLong())
        ).map { it.toProbeResponseIoc(iocConverter) }
        return IocPayload.Response.LatestIocs(
            releaseDatetime = Instant.now(),
            iocs = iocs
        )
    }

}

private fun Ioc.toProbeResponseIoc(iocEntryConverter: IocConverter) = IocPayload.IocForProbe(
    id = this.id,
    name = this.name,
    definition = this.definition.toResponseIocEntry(iocEntryConverter)
)

private fun Ioc.toResponseIoc(iocEntryConverter: IocConverter) = IocPayload.Ioc(
    id = this.id,
    name = this.name,
    definition = this.definition.toResponseIocEntry(iocEntryConverter),
    updated = this.updated,
    created = this.created
)

private fun Ioc.toSaveResponseIoc(iocEntryConverter: IocConverter) = IocPayload.SaveIoc(
    id = this.id,
    name = this.name,
    definition = this.definition.toResponseIocEntry(iocEntryConverter)
)

private fun IocEntry.toResponseIocEntry(iocEntryConverter: IocConverter) =
    iocEntryConverter.probeIocEntryRec(this)


private class IocConverter {

    fun probeIocEntryRec(entry: IocEntry): IocPayload.IocEntry {
        return IocPayload.IocEntry(
            name = entry.name,
            evalPolicy = entry.evalPolicy.name.let { IocPayload.EvaluationPolicy.valueOf(it) },
            childEvalPolicy = entry.childEvalPolicy.name.let { IocPayload.EvaluationPolicy.valueOf(it) },
            processCheck = entry.processCheck?.let { processCheck ->
                IocPayload.ProcessInfo(
                    name = processCheck.name,
                    hash = processCheck.hash?.let { hashed ->
                        IocPayload.Hashed(
                            algorithm = hashed.algorithm.name.let { IocPayload.Hashed.Type.valueOf(it) },
                            value = hashed.value
                        )
                    },
                    search = processCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
                )
            },
            mutexCheck = entry.mutexCheck?.let { mutexCheck ->
                IocPayload.MutexInfo(
                    name = mutexCheck.name
                )
            },
            dnsCheck = entry.dnsCheck?.let { dnsCheck ->
                IocPayload.DnsInfo(
                    name = dnsCheck.name
                )
            },
            connsCheck = entry.connsCheck?.let { connsCheck ->
                IocPayload.ConnsInfo(
                    name = connsCheck.name,
                    search = connsCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
                )
            },
            certsCheck = entry.certsCheck?.let { certCheck ->
                IocPayload.CertsInfo(
                    name = certCheck.name
                )
            },
            fileCheck = entry.fileCheck?.let { fileCheck ->
                IocPayload.FileInfo(
                    search = fileCheck.search.name.let { IocPayload.SearchType.valueOf(it) },
                    name = fileCheck.name,
                    hash = fileCheck.hash?.let { hashed ->
                        IocPayload.Hashed(
                            algorithm = hashed.algorithm.name.let { IocPayload.Hashed.Type.valueOf(it) },
                            value = hashed.value
                        )
                    }
                )
            },
            registryCheck = entry.registryCheck?.let { registryCheck ->
                IocPayload.RegistryInfo(
                    key = registryCheck.key,
                    value = registryCheck.value,
                    valueName = registryCheck.valueName,
                    search = registryCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
                )
            },
            offspring = if (entry.offspring?.isEmpty() != false) emptyList() else entry.offspring.map {
                probeIocEntryRec(it)
            }
        )
    }

    fun iocEntryToPayloadRec(entry: IocPayload.IocEntry): IocEntry {
        return IocEntry(
            name = entry.name,
            evalPolicy = entry.evalPolicy.name.let { IocEntry.EvaluationPolicy.valueOf(it) },
            childEvalPolicy = entry.childEvalPolicy.name.let { IocEntry.EvaluationPolicy.valueOf(it) },
            processCheck = entry.processCheck?.let { processCheck ->
                IocEntry.ProcessInfo(
                    name = processCheck.name,
                    hash = processCheck.hash?.let { hashed ->
                        IocEntry.Hashed(
                            algorithm = hashed.algorithm.name.let { IocEntry.Hashed.Type.valueOf(it) },
                            value = hashed.value
                        )
                    },
                    search = processCheck.search.name.let { IocEntry.SearchType.valueOf(it) }
                )
            },
            mutexCheck = entry.mutexCheck?.let { mutexCheck ->
                IocEntry.MutexInfo(
                    name = mutexCheck.name
                )
            },
            dnsCheck = entry.dnsCheck?.let { dnsCheck ->
                IocEntry.DnsInfo(
                    name = dnsCheck.name
                )
            },
            connsCheck = entry.connsCheck?.let { connsCheck ->
                IocEntry.ConnsInfo(
                    name = connsCheck.name,
                    search = connsCheck.search.name.let { IocEntry.SearchType.valueOf(it) }
                )
            },
            certsCheck = entry.certsCheck?.let { certCheck ->
                IocEntry.CertsInfo(
                    name = certCheck.name
                )
            },
            fileCheck = entry.fileCheck?.let { fileCheck ->
                IocEntry.FileInfo(
                    search = fileCheck.search.name.let { IocEntry.SearchType.valueOf(it) },
                    name = fileCheck.name,
                    hash = fileCheck.hash?.let { hashed ->
                        IocEntry.Hashed(
                            algorithm = hashed.algorithm.name.let { IocEntry.Hashed.Type.valueOf(it) },
                            value = hashed.value
                        )
                    }
                )
            },
            registryCheck = entry.registryCheck?.let { registryCheck ->
                IocEntry.RegistryInfo(
                    search = registryCheck.search.name.let { IocEntry.SearchType.valueOf(it) },
                    key = registryCheck.key,
                    value = registryCheck.value,
                    valueName = registryCheck.valueName
                )
            },
            offspring = if (entry.offspring?.isEmpty() != false) emptyList() else entry.offspring.map {
                iocEntryToPayloadRec(it)
            }
        )
    }
}
