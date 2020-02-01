package com.lifars.ioc.server.database.repository

import org.jetbrains.exposed.sql.Query

data class Page<T>(
    val data: List<T>,
    val totalSize: Int,
    val offset: Int
) : List<T> by data{
    val indexOfNextData = data.count() + offset
}

data class Pagination(
    val offset: Int,
    val limit: Int
){
    companion object {
        val default = Pagination(0, Int.MAX_VALUE)
    }
}

fun <T> List<T>.asPage(totalSize: Int, offset: Int) = Page(this, totalSize, offset)

fun Query.limit(pagination: Pagination) = this.limit(pagination.limit, pagination.offset)