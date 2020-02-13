package com.lifars.ioc.server.database

import net.openhft.chronicle.map.ChronicleMap
import java.io.File
import java.util.concurrent.ConcurrentMap

abstract class ChronicleMapDatabase<K, V>(
    private val keyClass: Class<K>,
    private val valueClass: Class<V>,
    private val name: String,
    private val storageFile: File,
    private val sampleKey: K,
    private val sampleValue: V
) : ConcurrentMap<K, V> by ChronicleMap
    .of(keyClass, valueClass)
    .name(name)
    .entries(50_000)
    .averageKey(sampleKey)
    .averageValue(sampleValue)
    .createOrRecoverPersistedTo(
        storageFile.apply {
            parentFile.mkdirs()
            createNewFile()
        },
        false
    )
