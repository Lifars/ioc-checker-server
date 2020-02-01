package com.lifars.ioc.server.routes

import com.lifars.ioc.server.service.IocService
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
fun Route.ioc(
    iocService: IocService
){
//    get<ClientLocations.Api.GetIocs>{
//        val request = call.receive<IocPayload.Request.Iocs>()
//        call.respond(iocService.pageIocs(request))
//    }
}