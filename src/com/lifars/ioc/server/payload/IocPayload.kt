package com.lifars.ioc.server.payload

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

object IocPayload {

    object Response {
        data class LatestIocs(
            val releaseDatetime: Instant,
            val iocs: List<IocForProbe>
        )
    }

    object Request {
        data class Iocs(
            val limit: Int,
            val offset: Int
        )

        data class Create(
            override val data: SaveIoc
        ) : Payload.Request.Create<SaveIoc>

        data class Update(
            override val id: Long,
            override val data: SaveIoc,
            override val previousData: SaveIoc
        ) : Payload.Request.Update<SaveIoc>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: SaveIoc
        ) : Payload.Request.UpdateMany<SaveIoc>
    }

    data class IocForProbe(
        val id: Long,
        val name: String,
        val definition: IocEntry
    )

//    data class Ioc(
//        @Json(index = 1) val id: Long,
//        @Json(index = 2) val name: String,
//        @Json(index = 4) val definition: IocEntry,
//        @Json(index = 3) val created: Instant,
//        @Json(index = 5) val updated: Instant
//    )

    data class Ioc(
        val id: Long,
        val name: String,
        val definition: IocEntry,
        val created: Instant,
        val updated: Instant
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SaveIoc(
        val id: Long,
        val name: String,
        val definition: IocEntry
    )

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
    )

    enum class EvaluationPolicy {
        ALL,
        ONE;

        companion object {
            val default = ONE
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
        val data: List<String>
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

//    data class IocEntry(
//        @Json(index = 1) val id: Long,
//        @Json(index = 3) val evalPolicy: EvaluationPolicy,
//        @Json(index = 4) val searchType: SearchType,
//        @Json(index = 2) val name: String?,
//        @Json(index = 12) val childEvalPolicy: EvaluationPolicy,
//        @Json(index = 13) val offspring: List<IocEntry>?,
//        @Json(index = 5) val registryCheck: RegistryInfo?,
//        @Json(index = 6) val fileCheck: FileInfo?,
//        @Json(index = 8) val mutexCheck: Boolean,
//        @Json(index = 7) val processCheck: Boolean,
//        @Json(index = 9) val dnsCheck: Boolean,
//        @Json(index = 10) val connsCheck: Boolean,
//        @Json(index = 11) val certsCheck: Boolean
//    )

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