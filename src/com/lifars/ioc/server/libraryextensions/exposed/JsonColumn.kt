package com.lifars.ioc.server.libraryextensions.exposed

import com.lifars.ioc.server.serialization.DefaultJsonConverter
import com.lifars.ioc.server.serialization.JsonConverter
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject
import kotlin.reflect.KClass

fun <T : Any> Table.json(
    name: String,
    klass: KClass<T>,
    jsonConverter: JsonConverter = DefaultJsonConverter
): Column<T> = registerColumn(name, JsonColumnType(klass, jsonConverter))

private class JsonColumnType<out T : Any>(
    private val klass: KClass<T>,
    private val jsonConverter: JsonConverter
) : ColumnType() {
    override fun sqlType(): String = "json"

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val obj = PGobject()
        obj.type = "json"
        obj.value = value as String
        stmt[index] = obj
    }

    override fun valueFromDB(value: Any): Any {
        val json = when (value) {
            is String -> {
                value
            }
            !is PGobject -> {
                return value
            }
            else -> {
                value.value
            }
        }

        return try {
            jsonConverter.fromJson(json, klass)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Can't parse JSON: $value")
        }
    }

    override fun notNullValueToDB(value: Any): Any = jsonConverter.toJson(value)
    override fun nonNullValueToString(value: Any): String = "'${jsonConverter.toJson(value)}'"
}


//private class JsonAsText<out T : Any>(
//    private val klass: KClass<T>,
//    private val jsonConverter: JsonConverter
//) : ColumnType() {
//    private val impl = TextColumnType()
//    override fun sqlType(): String = impl.sqlType()
//
//    override fun valueFromDB(value: Any): Any {
//        value as String
//        return try {
//            jsonConverter.fromJson(value, klass)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw RuntimeException("Can't parse JSON: $value")
//        }
//    }
//
//    override fun notNullValueToDB(value: Any): Any = jsonConverter.toJson(value)
//    override fun nonNullValueToString(value: Any): String = "'${jsonConverter.toJson(value)}'"
//}