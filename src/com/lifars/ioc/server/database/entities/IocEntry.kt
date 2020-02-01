package com.lifars.ioc.server.database.entities

data class IocEntry(
    val name: String? = null,
    val searchType: SearchType = SearchType.EXACT,
    val evalPolicy: EvaluationPolicy = EvaluationPolicy.default,
    val childEvalPolicy: EvaluationPolicy = EvaluationPolicy.default,
    val offspring: List<IocEntry>? = null,
    val registryCheck: RegistryInfo? = null,
    val fileCheck: FileInfo? = null,
    val mutexCheck: Boolean = false,
    val processCheck: Boolean = false,
    val dnsCheck: Boolean = false,
    val connsCheck: Boolean = false,
    val certsCheck: Boolean = false
){
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
        val key: String,
        val valueName: String,
        val value: String?
    )

    data class FileInfo(
        val name: String,
        val hash: Hashed? = null
    )

    data class Hashed(
        val algorithm: Type,
        val value: String
    ){
        enum class Type{
            MD5,
            SHA1,
            SHA256
        }
    }
}

