package com.lifars.ioc.server.database.repository

import org.jetbrains.exposed.sql.*

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

data class Filter(
    val items: Collection<Item>
) {
    data class Item(
        val column: String,
        val value: Any
    )
}

fun Table.select(filter: Filter?): Query = if (filter == null || filter.items.isEmpty())
    this.selectAll()
else {
    filter.buildExpression(this)
        .let { this.select(it) }
}

fun Filter.buildExpression(table: Table) =
    items
        .map { filterItem ->
            val columnName: String = if (filterItem.column == "id") "_id" else filterItem.column
            val column = table.columns.first { it.name == columnName }
            val columnType = column.columnType

            when(filterItem.value){
                is String -> {
                    val valueWrapped = QueryParameter("%${filterItem.value}%", columnType)
                    LikeOp(column, valueWrapped)
                }
                else -> {
                    val valueWrapped = QueryParameter(filterItem.value, columnType)
                    EqOp(column, valueWrapped)
                }
            }
        }.let { AndOp(it) }




