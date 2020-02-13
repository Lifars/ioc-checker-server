package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.FeedSource
import com.lifars.ioc.server.database.repository.FeedSourceRepository
import com.lifars.ioc.server.database.repository.IocRepository
import com.lifars.ioc.server.database.tables.keyvalue.VisitedFeeds
import com.lifars.ioc.server.feed.MispIocHarvester
import com.lifars.ioc.server.feed.converter.MispConverter
import io.ktor.client.HttpClient
import mu.KotlinLogging

class FeedService(
    private val feedSourceRepository: FeedSourceRepository,
    private val iocRepository: IocRepository,
    private val visitedFeedUrls: VisitedFeeds,
    private val httpClientFactory: () -> HttpClient
) {
    private val logger = KotlinLogging.logger {  }

    suspend fun harvestFeeds() {
        val feedSources = feedSourceRepository.findAll()
        if (feedSources.isEmpty()) {
            return
        }

        val mispSources = feedSources.filter {
            it.type == FeedSource.FeedType.MISP ||
                    it.type == FeedSource.FeedType.AUTO_DETECT
        }.map {
            it.url
        }

        val mispHarvester = MispIocHarvester(
            mispSources,
            visitedFeedUrls,
            httpClientFactory
        )
        val mispConverter = MispConverter()
        logger.info { "Starting IOC feed harvest. Number of specified sources is ${feedSources.size}" }
        val harvested =mispHarvester.harvest()
            .map { mispConverter.convert(it) }
        harvested.forEach { iocRepository.create(it) }
        logger.info { "IOC feed finished. Harvested ${harvested.size} items" }
    }
}