package com.lifars.ioc.server.service

fun <T : Comparable<T>> minimum(vararg vals: T): T {
    if (vals.isEmpty()) throw IllegalArgumentException("Cannot find minimum of nothing")
    return vals.min()!!
}

fun <T : Comparable<T>> maximum(vararg vals: T): T {
    if (vals.isEmpty()) throw IllegalArgumentException("Cannot find maximum of nothing")
    return vals.max()!!
}

fun String.isHexMd5(): Boolean {
    if (this.length != 32) return false
    return this.matches("^[a-fA-F0-9]{32}$".toRegex())
}

fun String.isHexSha1(): Boolean {
    if (this.length != 40) return false
    return this.matches("^[a-fA-F0-9]{40}$".toRegex())
}

fun String.isHexSha256(): Boolean {
    if (this.length != 64) return false
    return this.matches("^[a-fA-F0-9]{64}$".toRegex())
}
