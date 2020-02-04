package com.lifars.ioc.server.database.entities

data class IocEntry(
    val name: String? = null,
    val evalPolicy: EvaluationPolicy = EvaluationPolicy.default,
    val childEvalPolicy: EvaluationPolicy = EvaluationPolicy.default,
    val offspring: List<IocEntry>? = null,
    val registryCheck: RegistryInfo? = null,
    val fileCheck: FileInfo? = null,
    val mutexCheck: MutexInfo? = null,
    val processCheck: ProcessInfo? = null,
    val dnsCheck: DnsInfo? = null,
    val connsCheck: ConnsInfo? = null,
    val certsCheck: CertsInfo? = null
) {
    enum class EvaluationPolicy {
        ALL,
        ONE;

        companion object {
            val default = ALL
        }
    }

    enum class SearchType {
        EXACT,
        REGEX;

        companion object {
            val default = EXACT
        }
    }

    data class RegistryInfo(
        val search: SearchType = SearchType.default,
        val key: String,
        val valueName: String,
        val value: String?
    )

    data class FileInfo(
        val search: SearchType = SearchType.default,
        val name: String,
        val hash: Hashed? = null
    )

    data class MutexInfo(
        val data: List<String>
    )

    data class ProcessInfo(
        val search: SearchType = SearchType.default,
        val hash: Hashed? = null,
        val data: List<String>? = null
    )

    data class DnsInfo(
        val name: String
    )

    enum class ConnSearchType {
        IP,
        EXACT,
        REGEX;
    }

    data class ConnsInfo(
        val search: ConnSearchType,
        val data: List<String>
    )

    enum class CertSearchType {
        DOMAIN,
        ISSUER
    }

    data class CertsInfo(
        val search: CertSearchType,
        val data: List<String>
    )

    data class Hashed(
        val algorithm: Type,
        val value: String
    ) {
        enum class Type {
            MD5,
            SHA1,
            SHA256
        }
    }
}

