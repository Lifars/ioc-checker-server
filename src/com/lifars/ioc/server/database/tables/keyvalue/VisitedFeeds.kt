package com.lifars.ioc.server.database.tables.keyvalue

import com.lifars.ioc.server.database.ChronicleMapDatabase
import java.io.File
import java.time.Instant

class VisitedFeeds(storageFile: File): ChronicleMapDatabase<String, String>(
    keyClass = String::class.java,
    valueClass = String::class.java,
    name = "VisitedFeeds",
    sampleKey = "https://www.sample.lu/doc/misp/feed-osint/542e4c9c-cadc-4f8f-bb11-6d13950d210b.json",
    sampleValue = Instant.now().toEpochMilli().toString(),
    storageFile = storageFile
)