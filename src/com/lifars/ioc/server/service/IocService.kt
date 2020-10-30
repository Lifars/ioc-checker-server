package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.database.repository.IocRepository
import com.lifars.ioc.server.database.repository.Pagination
import com.lifars.ioc.server.database.repository.Sort
import com.lifars.ioc.server.payload.IocPayload
import java.time.Instant

class IocService(
    override val repository: IocRepository
) : CrudService<Ioc, IocPayload.Ioc, IocPayload.SaveIoc> {


    private val iocConverter = IocConverter()

    override fun Ioc.toDto(): IocPayload.Ioc = this.toResponseIoc(iocConverter)

    fun IocPayload.Ioc.toEntity() = iocConverter.frontendIocToEntity(this)

    override fun Ioc.toSavedDto() = toSaveResponseIoc(iocConverter)

    override fun IocPayload.SaveIoc.toSaveEntity(): Ioc = IocPayload.Ioc(
        id = 0,
        evalPolicy = evalPolicy ?: IocPayload.EvaluationPolicy.ONE,
        searchCerts = searchCerts ?: emptyList(),
        searchConns = searchConns ?: emptyList(),
        searchDns = searchDns ?: emptyList(),
        searchMutexes = searchMutexes ?: emptyList(),
        searchPaths = searchPaths ?: emptyList(),
        searchProcesses = searchProcesses ?: emptyList(),
        searchRegistries = searchRegistries ?: emptyList(),
        updated = Instant.now(),
        name = name ?: "IOC-${Instant.now()}",
        created = Instant.now()
    ).let { iocConverter.frontendIocToEntity(it) }

    suspend fun iocsForProbe(page: Int): IocPayload.Response.Probe.Iocs {
        val iocConverter = IocConverter()
        val pageLimit = 150
        val iocsPage = repository.find(
            pagination = Pagination((pageLimit * page), pageLimit),
            sort = Sort("id", Sort.Order.DESC)
        )
        val iocs = iocsPage.map { it.toProbeResponseIoc(iocConverter) }
        return IocPayload.Response.Probe.Iocs(
            releaseDatetime = Instant.now(),
            iocs = iocs,
            totalIocs = iocsPage.totalSize.toLong()
        )
    }
}

private fun Ioc.toProbeResponseIoc(iocEntryConverter: IocConverter) =
    IocPayload.Probe.Ioc(
        id = this.id,
        name = this.name,
        definition = this.definition.toResponseIocEntry(iocEntryConverter)
    )

private fun Ioc.toResponseIoc(iocEntryConverter: IocConverter) = iocEntryConverter.entityIocToFrontend(this)

private fun Ioc.toSaveResponseIoc(iocEntryConverter: IocConverter) =
    toResponseIoc(iocEntryConverter).let {
        IocPayload.SaveIoc(
            id = it.id,
            name = it.name,
            searchRegistries = it.searchRegistries,
            searchProcesses = it.searchProcesses,
            searchPaths = it.searchPaths,
            searchMutexes = it.searchMutexes,
            searchDns = it.searchDns,
            searchConns = it.searchConns,
            searchCerts = it.searchCerts,
            evalPolicy = it.evalPolicy
        )
    }
//    IocPayload.SaveIoc(
//    id = this.id.value,
//    name = this.name,
//    definition = this.definition.toResponseIocEntry(iocEntryConverter)
//)

private fun IocEntry.toResponseIocEntry(iocEntryConverter: IocConverter) =
    iocEntryConverter.probeIocEntryRec(this)


private class IocConverter {

    fun frontendIocToEntity(request: IocPayload.Ioc): Ioc {
        val entry = frontendIocToEntry(request)
        return Ioc(
            id = request.id,
            name = request.name,
            created = request.created,
            updated = request.updated,
            definition = entry
        )
    }

    fun frontendIocToEntry(request: IocPayload.Ioc): IocEntry {
        val evalPolicy = IocEntry.EvaluationPolicy.valueOf(request.evalPolicy.name)
        val maxCommonIndex = maximum(
            request.searchCerts.size,
            request.searchConns.size,
            request.searchDns.size,
            request.searchRegistries.size,
            request.searchPaths.size,
            request.searchProcesses.size,
            request.searchProcesses.size
        )
        val childEntries = mutableListOf<IocEntry>()
        for (i in 0 until maxCommonIndex) {
            childEntries += IocEntry(
                name = "Child-$i",
                childEvalPolicy = evalPolicy,
                evalPolicy = evalPolicy,
                offspring = null,
                certsCheck = request.searchCerts.getOrNull(i)?.let { IocEntry.CertsInfo(name = it) },
                mutexCheck = request.searchMutexes.getOrNull(i)?.let { IocEntry.MutexInfo(name = it) },
                processCheck = request.searchProcesses.getOrNull(i)?.let { process ->
                    IocEntry.ProcessInfo(
                        search = process.searchType(),
                        name = process.maybeRegex(),
                        hash = process.hashInfo()
                    )
                },
                connsCheck = request.searchConns.getOrNull(i)?.let { conn ->
                    IocEntry.ConnsInfo(name = conn, search = conn.searchType())
                },
                registryCheck = request.searchRegistries.getOrNull(i)?.let { registry ->
                    val keyAndValueName = registry.substringBeforeLast("=")
                    val key = keyAndValueName.substringBeforeLast("\\")
                    val valueName = keyAndValueName.substringAfterLast("\\")
                    val value = registry.substringAfterLast("=")
                    IocEntry.RegistryInfo(
                        search = registry.searchType(),
                        value = value.wildcardsToRegexString(),
                        valueName = valueName.wildcardsToRegexString(),
                        key = key.wildcardsToRegexString()
                    )
                },
                fileCheck = request.searchPaths.getOrNull(i)?.let { path ->
                    IocEntry.FileInfo(
                        search = path.searchType(),
                        name = path.maybeRegex(),
                        hash = path.hashInfo()
                    )
                },
                dnsCheck = request.searchDns.getOrNull(i)?.let { IocEntry.DnsInfo(name = it) }
            )
        }
        return IocEntry(
            name = "Entry-${request.name}",
            dnsCheck = null,
            fileCheck = null,
            registryCheck = null,
            connsCheck = null,
            processCheck = null,
            mutexCheck = null,
            certsCheck = null,
            offspring = childEntries,
            evalPolicy = evalPolicy,
            childEvalPolicy = evalPolicy
        )
    }

    private class WorkFrontendIoc(
        val searchPaths: MutableList<String>, // E + R + H
        val searchRegistries: MutableList<String>, // E + R
        val searchMutexes: MutableList<String>, // E
        val searchProcesses: MutableList<String>, // E + R + H
        val searchDns: MutableList<String>, // E
        val searchConns: MutableList<String>, // E + R
        val searchCerts: MutableList<String>// E
    ) {
        companion object {
            fun empty() = WorkFrontendIoc(
                searchCerts = mutableListOf(),
                searchConns = mutableListOf(),
                searchDns = mutableListOf(),
                searchMutexes = mutableListOf(),
                searchPaths = mutableListOf(),
                searchProcesses = mutableListOf(),
                searchRegistries = mutableListOf()
            )
        }
    }

    fun entityIocToFrontend(entity: Ioc): IocPayload.Ioc {
        val work = entityIocEntryToWork(entity.definition)
        return IocPayload.Ioc(
            id = entity.id,
            created = entity.created,
            name = entity.name,
            updated = entity.updated,
            searchRegistries = work.searchRegistries,
            searchProcesses = work.searchProcesses,
            searchPaths = work.searchPaths,
            searchMutexes = work.searchMutexes,
            searchDns = work.searchDns,
            searchConns = work.searchConns,
            searchCerts = work.searchCerts,
            evalPolicy = IocPayload.EvaluationPolicy.valueOf(entity.definition.evalPolicy.name)
        )
    }


    private fun entityIocEntryToWork(entry: IocEntry): WorkFrontendIoc {
        val work = WorkFrontendIoc.empty()
        entityIocEntryToWorkRec(entry, work)
        return work
    }

    private fun entityIocEntryToWorkRec(
        entry: IocEntry,
        work: WorkFrontendIoc
    ) {
        entry.certsCheck?.let { work.searchCerts += it.name }
        entry.connsCheck?.let { work.searchConns += it.name }
        entry.dnsCheck?.let { work.searchDns += it.name }
        entry.mutexCheck?.let { work.searchMutexes += it.name }
        entry.registryCheck?.let { registryInfo ->
            val registryInfoInOneLine = StringBuilder()
            val isRegex = registryInfo.search == IocEntry.SearchType.REGEX
            if (isRegex) {
                registryInfoInOneLine
                    .append(registryInfo.key.regexStringToWildcards())
                    .append("\\")
                    .append(registryInfo.valueName.regexStringToWildcards())
                registryInfo.value?.let { registryInfoInOneLine.append("=").append(it.regexStringToWildcards()) }
            } else {
                registryInfoInOneLine
                    .append(registryInfo.key)
                    .append("\\")
                    .append(registryInfo.valueName)
                registryInfo.value?.let { registryInfoInOneLine.append("=").append(it) }
            }
            work.searchRegistries += registryInfoInOneLine.toString()
        }
        entry.fileCheck?.let { fileInfo ->
            if (fileInfo.name == null && fileInfo.hash != null) {
                work.searchPaths += fileInfo.hash.value
            } else if (fileInfo.name != null) {
                work.searchPaths += if (fileInfo.search == IocEntry.SearchType.EXACT)
                    fileInfo.name
                else
                    fileInfo.name.regexStringToWildcards()
            }
        }
        entry.offspring?.forEach { entityIocEntryToWorkRec(it, work) }
    }

    private fun String.hashType(): IocEntry.Hashed.Type? = when {
        this.isHexMd5() -> IocEntry.Hashed.Type.MD5
        this.isHexSha1() -> IocEntry.Hashed.Type.SHA1
        this.isHexSha256() -> IocEntry.Hashed.Type.SHA256
        else -> null
    }

    private fun String.searchType(): IocEntry.SearchType = this.hashType()?.let { IocEntry.SearchType.EXACT }
        ?: if (containsWildcards()) IocEntry.SearchType.REGEX else IocEntry.SearchType.EXACT

    private fun String.hashInfo(): IocEntry.Hashed? =
        this.hashType()?.let { IocEntry.Hashed(algorithm = it, value = this) }

    private fun String.maybeRegex(): String? = if (this.hashType() == null) this.wildcardsToRegexString() else null

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


//class IocService(
//    override val repository: IocRepository
//) : CrudService<Ioc, IocPayload.Ioc, IocPayload.SaveIoc> {
//
//
//    private val iocConverter = IocConverter()
//
//    override fun Ioc.toDto(): IocPayload.Ioc = this.toResponseIoc(iocConverter)
//
//    fun IocPayload.Ioc.toEntity() = Ioc(
//        id = this.id,
//        updated = Instant.now(),
//        created = Instant.now(),
//        name = this.name,
//        definition = iocConverter.iocEntryToPayloadRec(this.definition)
//    )
//
//    override fun Ioc.toSavedDto() = toSaveResponseIoc(iocConverter)
//
//    override fun IocPayload.SaveIoc.toSaveEntity() = Ioc(
//        id = this.id,
//        name = this.name,
//        created = Instant.now(),
//        updated = Instant.now(),
//        definition = iocConverter.iocEntryToPayloadRec(this.definition)
//    )
//
//    suspend fun iocsForProbe(page: Int): IocPayload.Response.IocsForProbe {
//        val iocConverter = IocConverter()
//        val pageLimit = 150
//        val iocsPage = repository.find(
//            pagination =  Pagination(pageLimit * page, pageLimit),
//            sort = Sort("id", Sort.Order.DESC)
//        )
//        val iocs = iocsPage.map { it.toProbeResponseIoc(iocConverter) }
//        return IocPayload.Response.IocsForProbe(
//            releaseDatetime = Instant.now(),
//            iocs = iocs,
//            totalIocs = iocsPage.totalSize
//        )
//    }
//
//}
//
//private fun Ioc.toProbeResponseIoc(iocEntryConverter: IocConverter) = IocPayload.IocForProbe(
//    id = this.id,
//    name = this.name,
//    definition = this.definition.toResponseIocEntry(iocEntryConverter)
//)
//
//private fun Ioc.toResponseIoc(iocEntryConverter: IocConverter) = IocPayload.Ioc(
//    id = this.id,
//    name = this.name,
//    definition = this.definition.toResponseIocEntry(iocEntryConverter),
//    updated = this.updated,
//    created = this.created
//)
//
//private fun Ioc.toSaveResponseIoc(iocEntryConverter: IocConverter) = IocPayload.SaveIoc(
//    id = this.id,
//    name = this.name,
//    definition = this.definition.toResponseIocEntry(iocEntryConverter)
//)
//
//private fun IocEntry.toResponseIocEntry(iocEntryConverter: IocConverter) =
//    iocEntryConverter.probeIocEntryRec(this)
//
//
//private class IocConverter {
//
//    fun probeIocEntryRec(entry: IocEntry): IocPayload.IocEntry {
//        return IocPayload.IocEntry(
//            name = entry.name,
//            evalPolicy = entry.evalPolicy.name.let { IocPayload.EvaluationPolicy.valueOf(it) },
//            childEvalPolicy = entry.childEvalPolicy.name.let { IocPayload.EvaluationPolicy.valueOf(it) },
//            processCheck = entry.processCheck?.let { processCheck ->
//                IocPayload.ProcessInfo(
//                    name = processCheck.name,
//                    hash = processCheck.hash?.let { hashed ->
//                        IocPayload.Hashed(
//                            algorithm = hashed.algorithm.name.let { IocPayload.Hashed.Type.valueOf(it) },
//                            value = hashed.value
//                        )
//                    },
//                    search = processCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
//                )
//            },
//            mutexCheck = entry.mutexCheck?.let { mutexCheck ->
//                IocPayload.MutexInfo(
//                    name = mutexCheck.name
//                )
//            },
//            dnsCheck = entry.dnsCheck?.let { dnsCheck ->
//                IocPayload.DnsInfo(
//                    name = dnsCheck.name
//                )
//            },
//            connsCheck = entry.connsCheck?.let { connsCheck ->
//                IocPayload.ConnsInfo(
//                    name = connsCheck.name,
//                    search = connsCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
//                )
//            },
//            certsCheck = entry.certsCheck?.let { certCheck ->
//                IocPayload.CertsInfo(
//                    name = certCheck.name
//                )
//            },
//            fileCheck = entry.fileCheck?.let { fileCheck ->
//                IocPayload.FileInfo(
//                    search = fileCheck.search.name.let { IocPayload.SearchType.valueOf(it) },
//                    name = fileCheck.name,
//                    hash = fileCheck.hash?.let { hashed ->
//                        IocPayload.Hashed(
//                            algorithm = hashed.algorithm.name.let { IocPayload.Hashed.Type.valueOf(it) },
//                            value = hashed.value
//                        )
//                    }
//                )
//            },
//            registryCheck = entry.registryCheck?.let { registryCheck ->
//                IocPayload.RegistryInfo(
//                    key = registryCheck.key,
//                    value = registryCheck.value,
//                    valueName = registryCheck.valueName,
//                    search = registryCheck.search.name.let { IocPayload.SearchType.valueOf(it) }
//                )
//            },
//            offspring = if (entry.offspring?.isEmpty() != false) emptyList() else entry.offspring.map {
//                probeIocEntryRec(it)
//            }
//        )
//    }
//
//    fun iocEntryToPayloadRec(entry: IocPayload.IocEntry): IocEntry {
//        return IocEntry(
//            name = entry.name,
//            evalPolicy = entry.evalPolicy.name.let { IocEntry.EvaluationPolicy.valueOf(it) },
//            childEvalPolicy = entry.childEvalPolicy.name.let { IocEntry.EvaluationPolicy.valueOf(it) },
//            processCheck = entry.processCheck?.let { processCheck ->
//                IocEntry.ProcessInfo(
//                    name = processCheck.name,
//                    hash = processCheck.hash?.let { hashed ->
//                        IocEntry.Hashed(
//                            algorithm = hashed.algorithm.name.let { IocEntry.Hashed.Type.valueOf(it) },
//                            value = hashed.value
//                        )
//                    },
//                    search = processCheck.search.name.let { IocEntry.SearchType.valueOf(it) }
//                )
//            },
//            mutexCheck = entry.mutexCheck?.let { mutexCheck ->
//                IocEntry.MutexInfo(
//                    name = mutexCheck.name
//                )
//            },
//            dnsCheck = entry.dnsCheck?.let { dnsCheck ->
//                IocEntry.DnsInfo(
//                    name = dnsCheck.name
//                )
//            },
//            connsCheck = entry.connsCheck?.let { connsCheck ->
//                IocEntry.ConnsInfo(
//                    name = connsCheck.name,
//                    search = connsCheck.search.name.let { IocEntry.SearchType.valueOf(it) }
//                )
//            },
//            certsCheck = entry.certsCheck?.let { certCheck ->
//                IocEntry.CertsInfo(
//                    name = certCheck.name
//                )
//            },
//            fileCheck = entry.fileCheck?.let { fileCheck ->
//                IocEntry.FileInfo(
//                    search = fileCheck.search.name.let { IocEntry.SearchType.valueOf(it) },
//                    name = fileCheck.name,
//                    hash = fileCheck.hash?.let { hashed ->
//                        IocEntry.Hashed(
//                            algorithm = hashed.algorithm.name.let { IocEntry.Hashed.Type.valueOf(it) },
//                            value = hashed.value
//                        )
//                    }
//                )
//            },
//            registryCheck = entry.registryCheck?.let { registryCheck ->
//                IocEntry.RegistryInfo(
//                    search = registryCheck.search.name.let { IocEntry.SearchType.valueOf(it) },
//                    key = registryCheck.key,
//                    value = registryCheck.value,
//                    valueName = registryCheck.valueName
//                )
//            },
//            offspring = if (entry.offspring?.isEmpty() != false) emptyList() else entry.offspring.map {
//                iocEntryToPayloadRec(it)
//            }
//        )
//    }
//}
