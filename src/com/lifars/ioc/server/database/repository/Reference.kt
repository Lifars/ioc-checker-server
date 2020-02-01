package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.Database
import com.xenomachina.argparser.InvalidArgumentException
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select

//data class Filter<T>(
//    val column: Column<T>,
//    val value: T
//) {
//    constructor(
//        columnName: String,
//        value: T,
//        table: Table
//    ): this(
//        column = table.columns.first { it.name == columnName } as Column<T>,
//        value = value
//    )
//}
//
//fun <T> Table.select(filter: Filter<T>) = this.select {
//    filter.column eq filter.value
//}

data class Reference(
    val targetTable: String,
    val id: Long
)

fun Table.select(filter: Filter?, reference: Reference?) = if (reference == null)
    this.select(filter)
else {
    val targetTable = (Database.tablesByName[reference.targetTable]
        ?: throw InvalidArgumentException("Unknown table ${reference.targetTable}"))
    val joined = (this innerJoin targetTable)
    val joinedOn = joined.select { targetTable.id eq reference.id }
    if (filter == null) joinedOn
    else {
        joinedOn.andWhere {
            filter.buildExpression(this@select)
        }
    }
}
