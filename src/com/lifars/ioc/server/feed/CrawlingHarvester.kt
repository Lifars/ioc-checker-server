package com.lifars.ioc.server.feed

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.util.concurrent.ConcurrentMap

typealias TimeStamp = String

class CrawlingHarvester<ExternalIoc : Any>(
    override val urls: Iterable<String>,
    private val httpClientFactory: () -> HttpClient,
    private val alreadyVisitedUrls: ConcurrentMap<String, TimeStamp>? = null,
    private val supportedSuffix: String? = null,
    private val fromRaw: (rawData: String) -> ExternalIoc
) : Harvester<ExternalIoc> {

    private val logger = KotlinLogging.logger { }

    override fun harvest(): List<ExternalIoc> =
        httpClientFactory().use { httpClient ->
            urls.flatMap { harvestUrl(it, httpClient) }
        }

    private fun harvestUrl(url: String, httpClient: HttpClient): Iterable<ExternalIoc> {
        val document = Jsoup.connect(url).get()
        val links = supportedSuffix?.let { document.select("a[href$=$supportedSuffix]") } ?: document.select("a[href]")
        return runBlocking {
            links
                .asSequence()
                .map { it.attr("abs:href") }
                .also { iocLinks ->
                    if (alreadyVisitedUrls == null)
                        iocLinks
                    else
                        iocLinks.filterNot { it in alreadyVisitedUrls }
                }.asFlow() // for suspending functions
                .map { iocLink ->
                    alreadyVisitedUrls?.putIfAbsent(iocLink, System.currentTimeMillis().toString())
                    logger.debug { "Found IOC: $iocLink" }
                    httpClient.get<String>(iocLink)
                }.mapNotNull { maybeIoc ->
                    runCatching { fromRaw(maybeIoc) }
                        .onFailure { error ->
                            logger.debug { "Cannot parse MISP IOC. Skipping due to ${error.message}" }
                        }.getOrNull()
                }.toList()
        }
    }
}
