package com.sphericalchickens.aoc2025gem.day02

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputText
import kotlin.system.measureTimeMillis

fun main() {
    println("--- Advent of Code 2025, Day 2 (Gemini Refactor) ---")
    val input = readInputText("aoc2025/day02_input.txt")

    // Run the integrated test suite before solving
    runTests()

    // Solve Part 1
    val t1 = measureTimeMillis {
        val result = part1(input)
        println("Part 1: $result")
    }
    println("Part 1 runtime: ${t1}ms")

    // Solve Part 2
    val t2 = measureTimeMillis {
        val result = part2(input)
        println("Part 2: $result")
    }
    println("Part 2 runtime: ${t2}ms")
}

/**
 * Runs the unit tests for both parts using the provided test inputs.
 * This encapsulates the testing logic, keeping the main method clean.
 */
private fun runTests() {
    val testInput = """
        11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
        1698522-1698528,446443-446449,38593856-38593862,565653-565659,
        824824821-824824827,2121212118-2121212124
    """.trimIndent().replace("\n", "")

    println("🧪 Running Tests...")

    // Verify logic on specific values
    check("11-22", listOf(11L, 22L), parseRanges("11-22").findValidIds { it.toString().isEchoPattern() })
    check("38593856-38593862", listOf(38593859L), parseRanges("38593856-38593862").findValidIds { it.toString().isEchoPattern() })

    // Verify Part 1 Integration
    check("Part 1 Test Case 1", 1227775554L, part1(testInput))

    // Verify Part 2 Logic specifics
    check("2121212118-2121212124", listOf(2121212121L), parseRanges("2121212118-2121212124").findValidIds { it.toString().isPeriodicPattern() })

    // Verify Part 2 Integration
    check("Part 2 Test Case 1", 4174379265L, part2(testInput))

    println("✅ All tests passed!")
}

// --- Solution Logic ---

private fun part1(input: String): Long {
    // We filter ranges for numbers that satisfy the 'Echo' pattern (repeating halves).
    // Using sumOf avoids creating intermediate lists of results.
    return parseRanges(input).sumOf { range ->
        range.sumOf { id ->
            if (id.toString().isEchoPattern()) id else 0L
        }
    }
}

private fun part2(input: String): Long {
    // We filter ranges for numbers that satisfy the 'Periodic' pattern (repeating prefix).
    return parseRanges(input).sumOf { range ->
        range.sumOf { id ->
            if (id.toString().isPeriodicPattern()) id else 0L
        }
    }
}

/**
 * Parses a comma-separated string of ranges (e.g., "10-20,30-40") into a Sequence of LongRange.
 * Using Sequence allows lazy evaluation, processing one range at a time instead of loading all into memory.
 */
private fun parseRanges(input: String): Sequence<LongRange> {
    return input.splitToSequence(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { token ->
            val separatorIndex = token.indexOf('-')
            val start = token.take(separatorIndex).toLong()
            val end = token.substring(separatorIndex + 1).toLong()
            start..end
        }
}

/**
 * Helper for testing to retrieve specific IDs matching a predicate from a sequence of ranges.
 */
private fun Sequence<LongRange>.findValidIds(predicate: (Long) -> Boolean): List<Long> {
    return this.flatMap { range ->
        range.asSequence().filter(predicate)
    }.toList()
}

/**
 * Extension function to check if the string is composed of two identical halves (e.g., "123123").
 * This replaces the substring allocation strategy with an index-based comparison.
 */
private fun String.isEchoPattern(): Boolean {
    val len = this.length
    if (len % 2 != 0) return false

    val half = len / 2
    for (i in 0 until half) {
        if (this[i] != this[i + half]) return false
    }
    return true
}

/**
 * Extension function to check if the string is formed by repeating a prefix substring multiple times.
 * e.g., "121212" (repeats "12") -> true
 * e.g., "12121" -> false
 */
private fun String.isPeriodicPattern(): Boolean {
    val n = length
    // Try all possible substring lengths 'len' that could form the pattern.
    // 'len' must be a divisor of 'n' and at most half of 'n'.
    for (len in 1..n / 2) {
        if (n % len != 0) continue

        // Check if the string repeats with period 'len'.
        // We compare every character at index `i` with the character at `i % len`.
        // If they all match, the string is periodic with period `len`.
        var isPattern = true
        for (i in len until n) {
            if (this[i] != this[i % len]) {
                isPattern = false
                break
            }
        }
        if (isPattern) return true
    }
    return false
}