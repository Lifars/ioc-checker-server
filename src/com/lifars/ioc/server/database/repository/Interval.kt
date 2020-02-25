package com.lifars.ioc.server.database.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.asLiteral

data class Interval<T : Comparable<T>>(
    val begin: T,
    val end: T
)

infix fun <T : Comparable<T>, S : T> ExpressionWithColumnType<S>.between(interval: Interval<T>): Op<Boolean> =
    Between(this, asLiteral(interval.begin), asLiteral(interval.end))

infix fun <T : Comparable<T>, S : T> ExpressionWithColumnType<S>.maybeBetween(interval: Interval<T>?): Op<Boolean>? =
    interval?.let { this between it }

infix fun Op<Boolean>.andMaybe(other: Expression<Boolean>?): Op<Boolean> =
    if (other == null) this
    else (this and other)