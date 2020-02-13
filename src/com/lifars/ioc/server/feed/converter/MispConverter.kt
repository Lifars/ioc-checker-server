package com.lifars.ioc.server.feed.converter

import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.feed.formats.Misp
import com.lifars.ioc.server.feed.formats.MispAttribute
import java.time.Instant

class MispConverter : ExternalIocConverter<Misp> {
    override fun convert(externalIoc: Misp): Ioc {
        val misp = externalIoc.event
        val iocEntries = misp.attribute.map { mispAttribute ->
            IocEntry(
                evalPolicy = IocEntry.EvaluationPolicy.ALL,
                childEvalPolicy = IocEntry.EvaluationPolicy.ONE,

                fileCheck = when (mispAttribute.category) {
                    MispAttribute.Category.ArtifactsDropped,
                    MispAttribute.Category.ExternalAnalysis -> IocEntry.FileInfo(
                        name = when (mispAttribute.type) {
                            MispAttribute.Type.FILENAME -> mispAttribute.value
                            MispAttribute.Type.FILENAME_AND_MD5 -> mispAttribute.value.split("|").firstOrNull()
                            MispAttribute.Type.FILENAME_AND_SHA1 -> mispAttribute.value.split("|").firstOrNull()
                            MispAttribute.Type.FILENAME_ADN_SHA256 -> mispAttribute.value.split("|").firstOrNull()
                            else -> null
                        },
                        search = IocEntry.SearchType.EXACT,
                        hash = when (mispAttribute.type) {
                            MispAttribute.Type.MD5 -> IocEntry.Hashed(
                                algorithm = IocEntry.Hashed.Type.MD5,
                                value = mispAttribute.value
                            )
                            MispAttribute.Type.SHA1 -> IocEntry.Hashed(
                                algorithm = IocEntry.Hashed.Type.SHA1,
                                value = mispAttribute.value
                            )
                            MispAttribute.Type.SHA256 -> IocEntry.Hashed(
                                algorithm = IocEntry.Hashed.Type.SHA256,
                                value = mispAttribute.value
                            )
                            MispAttribute.Type.FILENAME_AND_MD5 -> mispAttribute.value
                                .split("|")
                                .lastOrNull()?.let { mispAttributeValue ->
                                    IocEntry.Hashed(
                                        algorithm = IocEntry.Hashed.Type.MD5,
                                        value = mispAttributeValue
                                    )
                                }
                            MispAttribute.Type.FILENAME_AND_SHA1 -> mispAttribute.value
                                .split("|")
                                .lastOrNull()?.let { mispAttributeValue ->
                                    IocEntry.Hashed(
                                        algorithm = IocEntry.Hashed.Type.SHA1,
                                        value = mispAttributeValue
                                    )
                                }
                            MispAttribute.Type.FILENAME_ADN_SHA256 -> mispAttribute.value
                                .split("|")
                                .lastOrNull()?.let { mispAttributeValue ->
                                    IocEntry.Hashed(
                                        algorithm = IocEntry.Hashed.Type.SHA256,
                                        value = mispAttributeValue
                                    )
                                }
                            else -> null
                        }
                    )
                    else -> null
                },

                registryCheck = when (mispAttribute.category) {
                    MispAttribute.Category.ArtifactsDropped,
                    MispAttribute.Category.ExternalAnalysis -> {
                        val regkeyAndValue = mispAttribute.value.split("\\", "/")
                        val regkey = regkeyAndValue.dropLast(1).joinToString("\\")
                        val (regValueName, regValue) = run {
                            val regValueNameAndValue = regkeyAndValue.last().split("|")
                            if (regValueNameAndValue.size > 1) {
                                regValueNameAndValue.first() to regValueNameAndValue.last()
                            } else {
                                regValueNameAndValue.first() to null
                            }
                        }
                        when (mispAttribute.type) {
                            MispAttribute.Type.REGKEY,
                            MispAttribute.Type.REGKEY_AND_VALUE -> IocEntry.RegistryInfo(
                                key = regkey,
                                valueName = regValueName,
                                value = regValue
                            )
                            else -> null
                        }
                    }
                    else -> null
                },

                connsCheck = when (mispAttribute.category) {
                    MispAttribute.Category.NetworkActivity,
                    MispAttribute.Category.ExternalAnalysis -> {
                        when (mispAttribute.type) {
                            MispAttribute.Type.IP_DST,
                            MispAttribute.Type.IP_SRC,
                            MispAttribute.Type.DOMAIN,
                            MispAttribute.Type.HOSTNAME,
                            MispAttribute.Type.URL,
                            MispAttribute.Type.URI -> IocEntry.ConnsInfo(
                                search = IocEntry.SearchType.EXACT,
                                name = mispAttribute.value
                            )
                            MispAttribute.Type.DOMAIN_AND_IP,
                            MispAttribute.Type.IP_DST_AND_PORT,
                            MispAttribute.Type.IP_SRC_AND_PORT -> IocEntry.ConnsInfo(
                                search = IocEntry.SearchType.EXACT,
                                name = mispAttribute.value.split("|").first()
                            )
                            else -> null
                        }
                    }
                    else -> null
                },

                dnsCheck = when (mispAttribute.category) {
                    MispAttribute.Category.NetworkActivity,
                    MispAttribute.Category.ExternalAnalysis -> {
                        when (mispAttribute.type) {
                            MispAttribute.Type.DOMAIN,
                            MispAttribute.Type.URL,
                            MispAttribute.Type.URI -> IocEntry.DnsInfo(
                                name = mispAttribute.value
                            )
                            MispAttribute.Type.DOMAIN_AND_IP,
                            MispAttribute.Type.IP_DST_AND_PORT,
                            MispAttribute.Type.IP_SRC_AND_PORT -> IocEntry.DnsInfo(
                                name = mispAttribute.value.split("|").first()
                            )
                            else -> null
                        }
                    }
                    else -> null
                },

                mutexCheck = when (mispAttribute.category) {
                    MispAttribute.Category.ArtifactsDropped -> {
                        when (mispAttribute.type) {
                            MispAttribute.Type.MUTEX -> IocEntry.MutexInfo(
                                name = mispAttribute.value
                            )
                            else -> null
                        }
                    }
                    else -> null
                },

                certsCheck = null,


                processCheck = null,

                offspring = null
            )
        }.filter { iocEntry ->
            iocEntry.certsCheck != null
                    || iocEntry.connsCheck != null
                    || iocEntry.dnsCheck != null
                    || iocEntry.fileCheck != null
                    || iocEntry.mutexCheck != null
                    || iocEntry.processCheck != null
                    || iocEntry.registryCheck != null
                    || iocEntry.offspring.isNullOrEmpty().not()
        }

        return Ioc(
            id = 0,
            name = misp.info,
            created = Instant.ofEpochSecond(misp.timestamp.toLong()),
            updated = Instant.ofEpochSecond(misp.timestamp.toLong()),
            definition = IocEntry(
                name = misp.info,
                childEvalPolicy = IocEntry.EvaluationPolicy.ONE,
                offspring = iocEntries
            )
        )
    }
}