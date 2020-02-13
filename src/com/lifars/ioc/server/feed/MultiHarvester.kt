package com.lifars.ioc.server.feed

class MultiHarvester<ExternalIoc: Any>(
    private val harvesters: Iterable<Harvester<ExternalIoc>>
): Harvester<ExternalIoc> {

    init {
        if(harvesters.count() == 0){
            throw IllegalArgumentException("Harvesters cannot be empty")
        }
    }

    override val urls: Iterable<String>
        get() = harvesters.flatMap { it.urls }

    override fun harvest(): List<ExternalIoc> =
        harvesters.flatMap { it.harvest() }

}