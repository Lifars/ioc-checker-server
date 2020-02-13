package com.lifars.ioc.server.feed.formats

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate

//https://github.com/MISP/misp-rfc/blob/master/misp-core-format/raw.md

data class Misp(
    @JsonProperty("Event") val event: MispEvent
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MispEvent(
//    val uuid: UUID,
//    val id: String?,
//    val published: Boolean,
    val info: String,
    @JsonProperty("threat_level_id")
    val threatLevelId: ThreatLevelId?,
    val analysis: AnalysisStatus?,
    val date: LocalDate,
    val timestamp: BigDecimal,
    @JsonProperty("Attribute")
    val attribute: List<MispAttribute>
//    @JsonProperty("publish_timestamp")
//    val publishTimestamp: BigDecimal,
//    val orgId: String

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MispAttribute(
    val category: Category,
    val type: Type,
    val value: String,
    @JsonProperty("to_ids")
    val takeAction: Boolean
){
    enum class Category{
        @JsonEnumDefaultValue
        UnsupportedByUs,
//        @JsonProperty("Antivirus detection")
//        AntivirusDetection,
        @JsonProperty("Artifacts dropped")
        ArtifactsDropped,
        @JsonProperty("External analysis")
        ExternalAnalysis,
        @JsonProperty("Network activity")
        NetworkActivity

    }
    enum class Type{
        @JsonEnumDefaultValue
        UnsupportedByUs,
//        @JsonProperty("link")
//        LINK,
//        @JsonProperty("comment")
//        COMMENT,
//        @JsonProperty("hex")
//        HEX,
//        @JsonProperty("attachment")
//        ATTACHMENT,
//        @JsonProperty("other")
//        OTHER,
//        @JsonProperty("anonymised")
//        ANONYMISED,
        @JsonProperty("md5")
        MD5,
        @JsonProperty("sha1")
        SHA1,
        @JsonProperty("sha256")
        SHA256,
        @JsonProperty("filename")
        FILENAME,
        @JsonProperty("filename|md5")
        FILENAME_AND_MD5,
        @JsonProperty("filename|sha1")
        FILENAME_AND_SHA1,
        @JsonProperty("filename|sha256")
        FILENAME_ADN_SHA256,
        @JsonProperty("regkey")
        REGKEY,
        @JsonProperty("regkey|value")
        REGKEY_AND_VALUE,
        @JsonProperty("mutex")
        MUTEX,
        @JsonProperty("ip-src")
        IP_SRC,
        @JsonProperty("ip-dst")
        IP_DST,
        @JsonProperty("ip-src|port")
        IP_SRC_AND_PORT,
        @JsonProperty("ip-dst|port")
        IP_DST_AND_PORT,
        @JsonProperty("hostname")
        HOSTNAME,
        @JsonProperty("domain")
        DOMAIN,
        @JsonProperty("domain|ip")
        DOMAIN_AND_IP,
        @JsonProperty("url")
        URL,
        @JsonProperty("uri")
        URI,
    }
}

enum class AnalysisStatus {
    @JsonProperty("0")
    INITIAL,
    @JsonProperty("1")
    ONGOING,
    @JsonProperty("2")
    COMPLETE,
}


enum class ThreatLevelId{
    @JsonProperty("1")
    HIGH,
    @JsonProperty("2")
    MEDIUM,
    @JsonProperty("3")
    LOW,
    @JsonProperty("4")
    UNDEFINED
}