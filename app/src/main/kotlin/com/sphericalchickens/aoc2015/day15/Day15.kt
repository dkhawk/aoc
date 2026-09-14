package com.sphericalchickens.aoc2015.day15

import com.sphericalchickens.utils.formatDuration
import kotlin.time.measureTimedValue

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
    val puzzleInput = readInputLines("aoc2015/day15_input.txt")
    println("\n--- Advent of Code 2015, Day XX ---")


    // --- Part 1: TBD ---
    val (part1Result, part1Duration) = measureTimedValue {
        part1(puzzleInput)
    }
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${formatDuration(part1Duration)}")


    // --- Part 2: TBD ---
    val (part2Result, part2Duration) = measureTimedValue { part2(puzzleInput) }
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${formatDuration(part2Duration)}")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
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
    """.trimIndent().lines()
    check(part1(testInput1) == 0)

    // Part 2 Test Cases
    val testInput2 = """
    """.trimIndent().lines()
    check(part2(testInput2) == 0)
}
