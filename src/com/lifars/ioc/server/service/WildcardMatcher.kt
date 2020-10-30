package com.lifars.ioc.server.service

import org.apache.commons.io.FilenameUtils

fun String.wildcardMatches(pattern: String): Boolean =
    FilenameUtils.wildcardMatch(this, pattern)

fun String.containsWildcards(): Boolean = "*" in this || "?" in this

fun String.wildcardsToRegexString(): String = if (!containsWildcards()) {
    this
} else {
//        "\\Q$this$\\E".replace("*", "\\E.*\\Q").replace("?", "\\")
    this.replace(".", "\\.").replace("*", ".*").replace("?", ".")
}

fun String.regexStringToWildcards(): String {
    val result = StringBuilder()
    this.forEachIndexed { index, char ->
        val isLast = index == this.length - 1
        val isFirst = index == 0
        val isOrdinary = !isLast && !isFirst
        val prevChar = this.getOrNull(index - 1)
        val nextChar = this.getOrNull(index + 1)
        val isStar = char == '*'
        val isDot = char == '.'
        val isBackSlash = char == '\\'

        val isLiteralBackSlash = isBackSlash && prevChar?.let { it == '\\' } ?: false
        val isLiteralDot = isDot && prevChar?.let { it == '\\' } ?: false
        val isLiteralStar = isStar && prevChar?.let { it == '\\' } ?: false

        val isDotStar = !isLiteralDot && nextChar?.let { it == '*' } ?: false

        val matchSingleCharacter = isDot && !isLiteralDot && !isDotStar
        val matchMultipleCharacters = isDotStar || isStar && !isLiteralStar

        val isLiteral = !matchSingleCharacter || !matchMultipleCharacters || isLiteralBackSlash

        when {
            isLiteral -> result.append(char)
            matchSingleCharacter -> result.append("?")
            matchMultipleCharacters -> result.append("*")
        }
    }
    return result.toString()
}
