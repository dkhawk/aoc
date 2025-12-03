package com.sphericalchickens.aoc2025gem.day03

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

// Define the target lengths as constants for clarity.
private const val PART_1_TARGET_LENGTH = 2
private const val PART_2_TARGET_LENGTH = 12

fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 3 ---")

    // The input is read as a list of strings, where each string represents a line of digits.
    val input = readInputLines("aoc2025/day03_input.txt")

    // The utility function 'check' is assumed to be available from the user's environment.

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
            part1(input)
        }
        println("   Part 1: $part1Result")
        println("Part 1 runtime: ${formatDuration(part1Duration)}")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            part2(input)
        }
        println("   Part 2: $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}")
    }
}

/**
 * Solves Part 1: Sums the result of the max 2-digit subsequence for each line.
 * Part 1 result is an Int because the max value is 99.
 */
private fun part1(input: List<String>): Int {
    // Using sumOf and the generalized function for conciseness.
    return input.sumOf { findMaxKDigitSubsequence(it, PART_1_TARGET_LENGTH).toInt() }
}

/**
 * Solves Part 2: Sums the result of the max 12-digit subsequence for each line.
 * Part 2 result is a Long because the sum of 12-digit numbers will exceed Int.MAX_VALUE.
 */
private fun part2(input: List<String>): Long {
    // Using sumOf and the generalized function. The result is already a Long.
    return input.sumOf { findMaxKDigitSubsequence(it, PART_2_TARGET_LENGTH) }
}

/**
 * Core logic: Finds the lexicographically largest subsequence of length K (targetLength).
 * This generalized function replaces the separate maxJoltage and maxJoltage12 functions.
 * The greedy strategy ensures that for each selected digit, there are enough
 * remaining characters in the string to complete the subsequence.
 *
 * @param s The input string of digits.
 * @param targetLength The desired length of the subsequence.
 * @return The subsequence as a Long.
 */
private fun findMaxKDigitSubsequence(s: String, targetLength: Int): Long {
    var currentStartIndex = 0
    val resultDigits = CharArray(targetLength) // Use an array for efficient building

    // We iterate exactly 'targetLength' times, one for each digit we need to find.
    for (k in 0 until targetLength) {
        // remainingDigitsToFind is the total count of digits, including the current one (K - k)
        val remainingDigitsToFind = targetLength - k

        // --- CORRECTED INDEX CALCULATION ---
        // The last index we can search is i_max = s.length - remainingDigitsToFind (inclusive).
        // Since Kotlin's 'until' is exclusive, we need the index i_max + 1.
        val searchEndIndex = s.length - remainingDigitsToFind + 1

        // --- Optimized Search Loop ---
        // Initialize with the character at the current start of the search window.
        var maxDigit = s[currentStartIndex]
        var maxIndex = currentStartIndex

        // Iterate through the window [currentStartIndex + 1, searchEndIndex)
        // This is a single pass for the current selection, avoiding expensive String operations.
        for (i in currentStartIndex + 1 until searchEndIndex) {
            val digit = s[i]
            // We use '>' to ensure that if a digit appears multiple times,
            // the *earliest* occurrence is chosen. This is critical for correctness
            // as it maximizes the remaining search space for subsequent digits.
            if (digit > maxDigit) {
                maxDigit = digit
                maxIndex = i
            }
        }

        // Store the found digit and update the search starting point for the next iteration.
        resultDigits[k] = maxDigit
        currentStartIndex = maxIndex + 1
    }

    // Idiomatic conversion: join the characters and convert to Long.
    return resultDigits.joinToString("").toLong()
}

// --- Test Implementation for Validation ---

private fun runPart1Tests() {
    val testInput = listOf(
        "987654321111111", // Expected: 98
        "811111111111119", // Expected: 89 (The case that failed previously)
        "234234234234278", // Expected: 78
        "818181911112111", // Expected: 92
    )
    val expected = listOf(98, 89, 78, 92)

    testInput.zip(expected).forEach { (line, expected) ->
        val result = findMaxKDigitSubsequence(line, PART_1_TARGET_LENGTH).toInt()
        check("Part 1: $line", expected, result)
    }

    check("Part 1 Test Case 1", 357, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = listOf(
        "987654321111111", // Expected: 987654321111
        "811111111111119", // Expected: 811111111119
        "234234234234278", // Expected: 434234234278
        "818181911112111", // Expected: 888911112111
    )
    val expected = listOf(987654321111L, 811111111119L, 434234234278L, 888911112111L)

    testInput.zip(expected).forEach { (line, expected) ->
        val result = findMaxKDigitSubsequence(line, PART_2_TARGET_LENGTH)
        check("Part 2: $line", expected, result)
    }

    check("Part 2 Test Case 1", 3121910778619L, part2(testInput))
}