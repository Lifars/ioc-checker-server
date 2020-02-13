package com.lifars.ioc.server.feed

interface Harvester<ExternalIoc: Any> {
    val urls: Iterable<String>

    fun harvest(): List<ExternalIoc>
}

