package com.sphericalchickens.aoc2015gem.day12b

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 12: JSAbacusFramework.io (Manual Parser)
 *
 * This program solves the puzzle for Day 12 of Advent of Code 2015.
 * This version implements a manual JSON parser, as requested, instead of using a pre-built library.
 */
fun main() {
    println("--- Advent of Code 2015, Day 12b (Gemini) ---")

    val puzzleInput = readInputLines("aoc2015/day12_input.txt").joinToString("")

    val part1Result = sumJson(puzzleInput, ignoreRed = false)
    println("🎁 Part 1: $part1Result")

    val part2Result = sumJson(puzzleInput, ignoreRed = true)
    println("🎀 Part 2: $part2Result")
}

/**
 * Parses a JSON string and calculates the sum of all numbers within it.
 *
 * @param jsonString The JSON string to parse.
 * @param ignoreRed If `true`, any object containing a property with the value "red" will be ignored.
 * @return The sum of all numbers in the JSON document.
 */
private fun sumJson(jsonString: String, ignoreRed: Boolean): Int {
    val iterator = PeekingIterator(jsonString.iterator())
    val token = parseToken(iterator)
    return token.value(ignoreRed)
}

/**
 * A sealed class representing a token in a JSON document.
 */
private sealed class JsonToken {
    /**
     * Calculates the numeric value of the token.
     *
     * @param ignoreRed If `true`, objects containing a "red" property will have a value of 0.
     * @return The numeric value of the token.
     */
    abstract fun value(ignoreRed: Boolean): Int
}

private data class JsonObject(val children: Map<String, JsonToken>) : JsonToken() {
    override fun value(ignoreRed: Boolean): Int {
        if (ignoreRed && children.values.any { it is JsonString && it.value == "red" }) {
            return 0
        }
        return children.values.sumOf { it.value(ignoreRed) }
    }
}

private data class JsonArray(val children: List<JsonToken>) : JsonToken() {
    override fun value(ignoreRed: Boolean) = children.sumOf { it.value(ignoreRed) }
}

private data class JsonNumber(val value: Int) : JsonToken() {
    override fun value(ignoreRed: Boolean) = value
}

private data class JsonString(val value: String) : JsonToken() {
    override fun value(ignoreRed: Boolean) = 0
}

/**
 * Parses the next token from the iterator.
 */
private fun parseToken(iterator: PeekingIterator<Char>): JsonToken {
    return when (iterator.peek()) {
        '{' -> parseObject(iterator)
        '[' -> parseArray(iterator)
        '"' -> parseString(iterator)
        else -> parseNumber(iterator)
    }
}

/**
 * Parses a JSON object from the iterator.
 */
private fun parseObject(iterator: PeekingIterator<Char>): JsonObject {
    iterator.next() // consume '{'
    val children = mutableMapOf<String, JsonToken>()
    while (iterator.peek() != '}') {
        val key = (parseString(iterator) as JsonString).value
        iterator.next() // consume ':'
        val value = parseToken(iterator)
        children[key] = value
        if (iterator.peek() == ',') {
            iterator.next() // consume ','
        }
    }
    iterator.next() // consume '}'
    return JsonObject(children)
}

/**
 * Parses a JSON array from the iterator.
 */
private fun parseArray(iterator: PeekingIterator<Char>): JsonArray {
    iterator.next() // consume '['
    val children = mutableListOf<JsonToken>()
    while (iterator.peek() != ']') {
        children.add(parseToken(iterator))
        if (iterator.peek() == ',') {
            iterator.next() // consume ','
        }
    }
    iterator.next() // consume ']'
    return JsonArray(children)
}

/**
 * Parses a JSON string from the iterator.
 */
private fun parseString(iterator: PeekingIterator<Char>): JsonString {
    iterator.next() // consume '"'
    val value = buildString {
        while (iterator.peek() != '"') {
            append(iterator.next())
        }
    }
    iterator.next() // consume '"'
    return JsonString(value)
}

/**
 * Parses a JSON number from the iterator.
 */
private fun parseNumber(iterator: PeekingIterator<Char>): JsonNumber {
    val value = buildString {
        if (iterator.peek() == '-') {
            append(iterator.next())
        }
        while (iterator.hasNext() && iterator.peek().isDigit()) {
            append(iterator.next())
        }
    }
    return JsonNumber(value.toInt())
}

/**
 * An iterator that allows peeking at the next element without consuming it.
 */
private class PeekingIterator<T>(private val iterator: Iterator<T>) : Iterator<T> {
    private var peeked: T? = null

    override fun hasNext(): Boolean {
        return peeked != null || iterator.hasNext()
    }

    override fun next(): T {
        if (peeked != null) {
            val result = peeked!!
            peeked = null
            return result
        }
        return iterator.next()
    }

    fun peek(): T {
        if (peeked == null) {
            peeked = iterator.next()
        }
        return peeked!!
    }
}