package com.lifars.ioc.server.database.repository

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table

//data class Sort(
//    val column: Column<*>,
//    val order: Order
//) {
//    enum class Order{
//        ASC,
//        DESC
//    }
//
//    constructor(
//        columnName: String,
//        order: String,
//        table: Table
//    ): this(
//        column = table.columns.first { it.name == columnName },
//        order = Order.valueOf(order)
//    )
//}

data class Sort(
    val column: String,
    val order: Order
) {
    enum class Order {
        ASC,
        DESC
    }
}


fun Query.orderBy(table: Table, sort: Sort?) =
    if (sort == null)
        this
    else {
        val column = if (sort.column == "id") "_id" else sort.column
        this.orderBy(
            column = table.columns.first { it.name == column },
            order = SortOrder.valueOf(sort.order.name)
        )
    }


