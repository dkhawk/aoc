package com.sphericalchickens.aoc2023.day19

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2023, Day 19: Aplenty
 *
 * Placeholder template for Day 19.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2023/day19_input.txt")
    println("\n--- Advent of Code 2023, Day 19: Aplenty ---")

    // --- Part 1 ---
    val (part1Result, part1Duration) = measureTimedValue {
        part1(puzzleInput)
    }
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${formatDuration(part1Duration)}")

    // --- Part 2 ---
    val (part2Result, part2Duration) = measureTimedValue {
        part2(puzzleInput)
    }
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${formatDuration(part2Duration)}")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {
    // TODO: Implement Part 1
    return 0
}

fun part2(input: List<String>): Int {
    // TODO: Implement Part 2
    return 0
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
        sample
    """.trimIndent().lines()

    // Non-failing stub checks
    check("Part 1 Sample", 0, part1(testInput))
    check("Part 2 Sample", 0, part2(testInput))
}
