package com.sphericalchickens.aoc2015.day22

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
    val puzzleInput = readInputLines("aoc2015/day22_input.txt")
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
