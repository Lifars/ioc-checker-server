package com.lifars.ioc.server.database.tables

object Iocs : BaseTable("Iocs") {
    val name = varchar("name", 255)
    val definition = text("definition")
}