package com.sphericalchickens.aoc2015.day12

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 12: JSAbacusFramework.io
 *
 * This program solves the puzzle for Day 12.
 *
 * Santa's Accounting-Elves need help balancing the books after a recent order. Unfortunately, their accounting software
 * uses a peculiar storage format. That's where you come in.
 *
 * They have a JSON document which contains a variety of things: arrays ([1,2,3]), objects ({"a":1, "b":2}), numbers,
 * and strings. Your first job is to simply find all the numbers throughout the document and add them together.
 *
 * For example:
 *
 * [1,2,3] and {"a":2,"b":4} both have a sum of 6.
 * [[[3]]] and {"a":{"b":4},"c":-1} both have a sum of 3.
 * {"a":[-1,1]} and [-1,{"a":1}] both have a sum of 0.
 * [] and {} both have a sum of 0.
 * You will not encounter any strings containing numbers.
 *
 * What is the sum of all numbers in the document?
 *
 * Your puzzle answer was 191164.
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day12_input.txt")
    println("\n--- Advent of Code 2015, Day XX ---")


    // --- Part 1: TBD ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")


    // --- Part 2: TBD ---
    val part2Result = part2(puzzleInput)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {
    val re = Regex("""-?\d+""")

    return input.sumOf {
        re.findAll(it).sumOf { result -> result.groups[0]!!.value.toInt() }
    }
}

fun part2(lines: List<String>): Int {
    val input = lines.joinToString(separator = "")

    return parse(input).sumOf { it.value() }
}

sealed class JSToken {
    abstract fun value(): Int
}

data class JSMap(
    val children: Map<String, JSToken>
) : JSToken() {
    operator fun get(o: String): JSToken? {
        return children[o]
    }

    override fun value(): Int {
        return if (children.values.any{ it == JSString("red") }) 0 else children.values.sumOf { it.value() }
    }
}

data class JSArray(val children: List<JSToken>) : JSToken() {
    operator fun get(i: Int): JSToken {
        return children[i]
    }

    override fun value() = children.sumOf { it.value() }
}

data class JSInt(val value: Int) : JSToken() {
    override fun value() = value
}

data class JSString(val value: String) : JSToken() {
    override fun value() = 0
}

private fun parse(input: String): List<JSToken> {
    val iterator = PeekingIterator(input.iterator())

    return parse(iterator)
}

private fun parse(iterator: PeekingIterator<Char>): List<JSToken> {
    return buildList<JSToken> {
        if (iterator.hasNext()) {
            add(parseToken(iterator))
        }
    }
}

private fun parseToken(iterator: PeekingIterator<Char>): JSToken {
    return when (iterator.peek()) {
        '[' -> parseArray(iterator)
        '{' -> parseMap(iterator)
        '"' -> parseString(iterator)
        else -> parseJsInt(iterator)
    }
}

private fun parseArray(iterator: PeekingIterator<Char>): JSArray {
    require(iterator.peek() == '[')
    iterator.next()
    val s = buildList<JSToken> {
        while (iterator.hasNext() && iterator.peek() != ']') {
            add(parseToken(iterator))
            val next = iterator.peek()
            if (next == ',') {
                // we have another token
                iterator.next()
            } else if (next == ']') {
                // finished
                iterator.next()  // toss out the closing ']'
                break
            } else {
                error("Unexpected token: \"$next\"")
            }
        }
    }
    return JSArray(s)
}

private fun parseMap(iterator: PeekingIterator<Char>): JSMap {
    require(iterator.peek() == '{')
    iterator.next()
    val s = buildMap<_root_ide_package_.kotlin.String, JSToken> {
        while (iterator.hasNext() && iterator.peek() != '}') {
            val k = parseToken(iterator) as JSString
            require(iterator.next() == ':')
            val v = parseToken(iterator)
            put(k.value, v)
            val next = iterator.peek()
            if (next == ',') {
                // we have another token
                iterator.next()
            } else if (next == '}') {
                // finished
                iterator.next()  // toss out the closing '}'
                break
            } else {
                error("Unexpected token: \"$next\"")
            }
        }
    }
    return JSMap(s)
}

private fun parseString(iterator: PeekingIterator<Char>): JSString {
    require(iterator.peek() == '"')
    iterator.next()
    val s = buildString {
        while (iterator.hasNext() && iterator.peek() != '"') {
            append(iterator.next())
        }
    }
    iterator.next()  // toss out the closing '"'
    return JSString(s)
}

private fun parseJsInt(iterator: PeekingIterator<Char>): JSInt {
    var result = 0
    var sign = 1
    while (iterator.hasNext() && (iterator.peek() in '0'..'9' || iterator.peek() == '-')) {
        val n = iterator.next()
        if (n == '-') {
            sign = -1
            continue
        }
        val i = n - '0'
        result = (result * 10) + i
    }

    return JSInt(result * sign)
}

class PeekingIterator<T>(private val iterator: Iterator<T>) : Iterator<T> {
    private var nextElement: T? = null
    private var hasPeeked = false

    override fun hasNext(): Boolean {
        return hasPeeked || iterator.hasNext()
    }

    override fun next(): T {
        if (hasPeeked) {
            hasPeeked = false
            return nextElement as T
        }
        return iterator.next()
    }

    fun peek(): T {
        if (!hasPeeked) {
            nextElement = iterator.next()
            hasPeeked = true
        }
        return nextElement as T
    }
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    check(
        parse("""{"c":"yellow","a":-10,"b":[71]}""").first().value() == 61
    )

    check(
        parse(""""abc"""").first() == JSString("abc")
    )

    check(
        parse("""123""").first() == JSInt(123)
    )

    check(
        parse("""[]""").first() == JSArray(listOf())
    )

    val p = parse("""[1,2,3,"a","b","c"]""")
    val a = p.first() as JSArray
    check(a[0] == JSInt(1))
    check(a[1] == JSInt(2))
    check(a[2] == JSInt(3))
    check(a[3] == JSString("a"))
    check(a[4] == JSString("b"))
    check(a[5] == JSString("c"))

    check(a.value() == 6)

    check(
        parse("""{}""").first() == JSMap(mapOf())
    )

    val p2 = parse("""{"a":1,"b":"red"}""")
    val m = p2.first() as JSMap
    check(m["a"] == JSInt(1))
    check(m["b"] == JSString("red"))

    // Part 1 Test Cases
    // * [1,2,3] and {"a":2,"b":4} both have a sum of 6.
    // * [[[3]]] and {"a":{"b":4},"c":-1} both have a sum of 3.
    // * {"a":[-1,1]} and [-1,{"a":1}] both have a sum of 0.
    // * [] and {} both have a sum of 0.
    val testInput1 = listOf(
        "[1,2,3]" to 6,
        """{"a":2,"b":4}""" to 6,
        """[[[3]]]""" to 3,
        """{"a":{"b":4},"c":-1}""" to 3,
        """{"a":[-1,1]}""" to 0,
        """[-1,{"a":1}]""" to 0,
        """[]""" to 0,
        """{}""" to 0,
    )

    for (input in testInput1) {
        check(part1(listOf(input.first)) == input.second) { "${input.first} != ${input.second}" }
    }

    // Part 2 Test Cases
    val testInput2 = """
        [1,2,3] 6
        [1,{"c":"red","b":2},3] 4
        {"d":"red","e":[1,2,3,4],"f":5} 0
        [1,"red",5] 6
        {"c":"yellow","a":-10,"b":[71]} 61
    """.trimIndent().lines().map { val l = it.split(" "); l[0] to l[1].toInt() }

    for (input in testInput2.drop(4)) {
        check(part2(listOf(input.first)) == input.second) { "${input.first} != ${input.second}" }
    }
}
