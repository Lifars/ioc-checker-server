package com.lifars.ioc.server.feed.converter

import com.lifars.ioc.server.database.entities.Ioc

interface ExternalIocConverter<T>{
    fun convert(externalIoc: T): Ioc
}