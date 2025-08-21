package com.sphericalchickens.app.aoc2015.day08

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day XX: TBD
 *
 * This program solves the puzzle for Day XX.
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day08_input.txt")
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
    val rawSize = input.sumOf { it.length }

    val memorySize = input.sumOf {
        it.unescape().length
    }

    return rawSize - memorySize
}

private fun String.unescape() : String {
    val result = substring(1, this.lastIndex)

    val iter = result.iterator()

    return buildString {
        while (iter.hasNext()) {
            val next = iter.nextChar()
            if (next != '\\') {
                append(next)
            } else {
                val escaped = iter.nextChar()
                if (escaped != 'x') {
                    // throw away the next character
                    append('?')
                } else {
                    iter.nextChar()
                    iter.nextChar()
                    append('*')
                }
            }
        }
    }
}

fun part2(input: List<String>): Int {
    val rawSize = input.sumOf { it.length }

    val memorySize = input.sumOf {
        it.escape().length
    }

    return memorySize - rawSize
}

private fun String.escape() : String {
    val iter = iterator()

    return buildString {
        append("\"")
        while (iter.hasNext()) {
            when (val c = iter.nextChar()) {
                '"' -> { append('\\'); append('"') }
                '\\' -> { append('\\'); append('\\') }
                else -> append(c)
            }
        }
        append("\"")
    }
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    val testInput1 = """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """.trimIndent().lines()

    check(testInput1[0].unescape().length == 0)
    check(testInput1[1].unescape().length == 3)
    check(testInput1[2].unescape().length == 7)
    check(testInput1[3].unescape().length == 1)

    check(testInput1[0].escape().length == 6)
    check(testInput1[1].escape().length == 9)
    check(testInput1[2].escape().length == 16)
    check(testInput1[3].escape().length == 11)
}
