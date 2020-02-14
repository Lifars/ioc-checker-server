package com.lifars.ioc.server.feed

import com.lifars.ioc.server.feed.formats.Misp
import com.lifars.ioc.server.serialization.DefaultJsonConverter.fromJson
import io.ktor.client.HttpClient
import java.util.concurrent.ConcurrentMap

class MispIocHarvester(
    urls: Iterable<String>,
    alreadyVisitedUrls: ConcurrentMap<String, TimeStamp>? = null,
    httpClientFactory: () -> HttpClient
) :
    Harvester<Misp> by CrawlingHarvester(
        urls = urls,
        alreadyVisitedUrls = alreadyVisitedUrls,
        supportedSuffix = ".json",
        httpClientFactory = httpClientFactory,
        fromRaw = { fromJson(it, Misp::class) }
    )