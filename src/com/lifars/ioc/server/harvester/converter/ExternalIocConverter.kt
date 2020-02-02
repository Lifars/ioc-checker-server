package com.lifars.ioc.server.harvester.converter

import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.harvester.JsonExternalIoc

interface ExternalJsonIocConverter{
    fun convert(externalIoc: JsonExternalIoc): Ioc
}