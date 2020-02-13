package com.lifars.ioc.server.database.tables.sql

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

abstract class BaseTable(name: String) : LongIdTable(name, "_id") {
    val created = timestamp("created").clientDefault { Instant.now() }
    val updated = timestamp("updated").clientDefault { Instant.now(); }
}

typealias BaseID = EntityID<Long>

//abstract class BaseEntity(
//    id: BaseID,
//    table: BaseTable
//) : LongEntity(id) {
//    val created by table.created
//    var updated by table.updated
//}
//
//abstract class UpdateHookedEntityClass<E : BaseEntity>(table: BaseTable) : LongEntityClass<E>(table) {
//
//    init {
//        EntityHook.subscribe { action ->
//            if (action.changeType == EntityChangeType.Updated) {
//                runCatching {
//                    action.toEntity(this)?.updated = Instant.now()
//                }
//            }
//        }
//    }
//}