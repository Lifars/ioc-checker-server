package com.lifars.ioc.server.routes.locations

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/api/admin")
class AdminLocations {
    @Location("/list")
    class List {
        @Location("/iocs")
        data class Ioc(val query: String)
        @Location("/probes")
        data class Probe(val query: String)
        @Location("/probe_reports")
        data class ProbeReport(val query: String)
        @Location("/users")
        data class User(val query: String)
        @Location("/feed_sources")
        data class FeedSource(val query: String)
        @Location("/found_iocs")
        data class FoundIoc(val query: String)
    }

    @Location("/one")
    class One{
        @Location("/iocs/{id}")
        data class Ioc(val id: Long)
        @Location("/probes/{id}")
        data class Probe(val id: Long)
        @Location("/probe_reports/{id}")
        data class ProbeReport(val id: Long)
        @Location("/users/{id}")
        data class User(val id: Long)
        @Location("/feed_sources/{id}")
        data class FeedSource(val id: Long)
        @Location("/found_iocs/{id}")
        data class FoundIoc(val id: Long)
    }

    @Location("/many")
    class Many{
        @Location("/iocs")
        data class Ioc(val query: String)
        @Location("/probes")
        data class Probe(val query: String)
        @Location("/probe_reports")
        data class ProbeReport(val query: String)
        @Location("/users")
        data class User(val query: String)
        @Location("/feed_sources")
        data class FeedSource(val query: String)
        @Location("/found_iocs")
        data class FoundIoc(val query: String)
    }

    @Location("/many_reference")
    class ManyReference{
        @Location("/iocs")
        data class Ioc(val query: String)
        @Location("/probes")
        data class Probe(val query: String)
        @Location("/probe_reports")
        data class ProbeReport(val query: String)
        @Location("/users")
        data class User(val query: String)
        @Location("/feed_sources")
        data class FeedSource(val query: String)
        @Location("/found_iocs")
        data class FoundIoc(val query: String)
    }

    @Location("/create")
    class Create{
        @Location("/iocs")
        class Ioc
        @Location("/probes")
        class Probe
        @Location("/probe_reports")
        class ProbeReport
        @Location("/users")
        class User
        @Location("/feed_sources")
        class FeedSource
        @Location("/found_iocs")
        class FoundIoc
    }

    @Location("/update")
    class Update{
        @Location("/iocs")
        class Ioc
        @Location("/probes")
        class Probe
        @Location("/probe_reports")
        class ProbeReport
        @Location("/users")
        class User
        @Location("/feed_sources")
        class FeedSource
        @Location("/found_iocs")
        class FoundIoc
    }

    @Location("/update_many")
    class UpdateMany{
        @Location("/iocs")
        class Ioc
        @Location("/probes")
        class Probe
        @Location("/probe_reports")
        class ProbeReport
        @Location("/users")
        class User
        @Location("/feed_sources")
        class FeedSource
        @Location("/found_iocs")
        class FoundIoc
    }

    @Location("/delete")
    class Delete{
        @Location("/iocs/{id}")
        data class Ioc(val id: Long)
        @Location("/probes/{id}")
        data class Probe(val id: Long)
        @Location("/probe_reports/{id}")
        data class ProbeReport(val id: Long)
        @Location("/users/{id}")
        data class User(val id: Long)
        @Location("/feed_sources/{id}")
        data class FeedSource(val id: Long)
        @Location("/found_iocs/{id}")
        data class FoundIoc(val id: Long)
    }

    @Location("/delete_many")
    class DeleteMany{
        @Location("/iocs")
        data class Ioc(val query: String)
        @Location("/probes")
        data class Probe(val query: String)
        @Location("/probe_reports")
        data class ProbeReport(val query: String)
        @Location("/users")
        data class User(val query: String)
        @Location("/feed_sources")
        data class FeedSource(val query: String)
        @Location("/found_iocs")
        data class FoundIoc(val query: String)
    }
}