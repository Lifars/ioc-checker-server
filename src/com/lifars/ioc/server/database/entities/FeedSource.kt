package com.lifars.ioc.server.database.entities

data class FeedSource(
    val id: Long,
    val url: String,
    val type: FeedType
){
    enum class FeedType{
        MISP,
        AUTO_DETECT
    }
}