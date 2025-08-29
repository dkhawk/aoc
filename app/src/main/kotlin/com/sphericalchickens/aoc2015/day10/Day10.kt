package com.sphericalchickens.aoc2015.day10

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 10: TBD
 *
 * This program solves the puzzle for Day 10.
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day10_input.txt")
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
    var result = input.first()

    for (ii in 0 until 40) {
        result = process(result)
    }

    return result.length
}

fun part2(input: List<String>): Int {
    var result = input.first()

    for (ii in 0 until 50) {
        result = process(result)
    }

    return result.length
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

private class IteratorWithPeek(private val data: String) {
    val iterator = data.iterator()
    var buffer: Char? = null

    fun hasNext(): Boolean {
        return buffer != null || iterator.hasNext()
    }

    fun nextChar(): Char {
        return if (buffer != null) {
            val c = buffer!!
            buffer = null
            c
        } else {
            iterator.nextChar()
        }
    }

    fun peekChar(): Char {
        if (buffer == null) {
            buffer = iterator.nextChar()
        }
        return buffer!!
    }
}

private fun process(line: String) : String {
    val iter = IteratorWithPeek(line)

    return buildString {
        while (iter.hasNext()) {
            val c = iter.nextChar()
            var count = 1

            while (iter.hasNext() && iter.peekChar() == c) {
                count++
                iter.nextChar()
            }

            append(count)
            append(c)
        }
    }
}

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    val testInput1 = """
         1 11
         11 21
         21 1211
         1211 111221
         111221 312211
    """.trimIndent().lines().map { it.trim() }.map { it.split(" ") }

    testInput1.forEach { input ->
        check(process(input[0]) == input[1]) { "process(${input[0]}) == ${input[1]}" }
    }

    // Part 2 Test Cases
//    val testInput2 = """
//    """.trimIndent().lines()
//    check(part2(testInput2) == 0)
}
