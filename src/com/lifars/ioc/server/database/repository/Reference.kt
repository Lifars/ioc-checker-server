package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.Database
import com.xenomachina.argparser.InvalidArgumentException
import org.jetbrains.exposed.sql.*

data class Reference(
    val targetTable: String,
    val id: Long
)

fun FieldSet.select(
    filter: Filter?,
    reference: Reference?,
    where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null
) = if (reference == null)
    this.select(filter, where)
else {
    val targetTable = (Database.tablesByName[reference.targetTable]
        ?: throw InvalidArgumentException("Unknown table ${reference.targetTable}"))
    val joined = (this.source.innerJoin(targetTable))
    val joinedOn = joined.select { targetTable.id eq reference.id }
    if (filter == null) joinedOn
    else {
        joinedOn.andWhere {
            filter.buildExpression(this@select, where)
        }
    }
}