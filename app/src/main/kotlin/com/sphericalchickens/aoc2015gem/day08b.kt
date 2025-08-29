package com.sphericalchickens.aoc2015gem

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 8: Matchsticks
 *
 * This puzzle involves calculating the difference between the number of characters in the
 * string literal representation of a string and its actual in-memory character count.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day08_input.txt")
    println("\n--- Advent of Code 2015, Day 8 ---")

    // --- Part 1: Difference between literal and in-memory length ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")

    // --- Part 2: Difference between newly encoded and original literal length ---
    val part2Result = part2(puzzleInput)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * Calculates the total number of characters in the code representation minus the
 * total number of characters in the in-memory representation.
 */
fun part1(input: List<String>): Int {
    // `sumOf` is an idiomatic and efficient way to transform and sum a collection.
    // Here, we calculate the difference for each line and sum the results.
    return input.sumOf { it.length - it.decodedCharCount() }
}

/**
 * Calculates the total number of characters in a new, fully encoded representation
 * minus the total number of characters in the original code representation.
 */
fun part2(input: List<String>): Int {
    return input.sumOf { it.encodedCharCount() - it.length }
}

/**
 * Calculates the number of characters a string literal occupies in memory after
 * parsing all escape sequences.
 *
 * This function avoids creating a new string, which is more memory and CPU efficient.
 * It manually iterates through the string content to count the "real" characters.
 */
private fun String.decodedCharCount(): Int {
    var memoryChars = 0
    // We iterate through the string's content, skipping the two enclosing quotes.
    var index = 1
    while (index < this.lastIndex) {
        if (this[index] == '\\') {
            // This is an escape sequence. Check what kind it is.
            index += when (this[index + 1]) {
                // A hex escape (e.g., "\x27") consumes 4 code characters.
                'x' -> 4
                // A simple escape (e.g., "\\" or "\"") consumes 2 code characters.
                else -> 2
            }
        } else {
            // This is a regular character, consuming 1 code character.
            index++
        }
        // Each pass through the loop, whether it's a regular char or an escape sequence,
        // represents exactly one character in memory.
        memoryChars++
    }
    return memoryChars
}

/**
 * Calculates the number of characters required to encode this string into a new,
 * safe string literal.
 *
 * The logic is simpler: start with the original length, add 2 for the new surrounding
 * quotes, and add 1 for each character that must now be escaped.
 */
private fun String.encodedCharCount(): Int {
    // The new length is the original length + 2 for new quotes + 1 for each character
    // that needs escaping (`"` and `\`). The `count` function is a perfect,
    // declarative way to express this.
    return this.length + 2 + this.count { it == '"' || it == '\\' }
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases from the puzzle.
 */
private fun runTests() {
    val testInput = """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """.trimIndent().lines()

    // Part 1 Test Cases
    check(testInput[0].decodedCharCount() == 0) { "Test 1.1 failed" }
    check(testInput[1].decodedCharCount() == 3) { "Test 1.2 failed" }
    check(testInput[2].decodedCharCount() == 7) { "Test 1.3 failed" }
    check(testInput[3].decodedCharCount() == 1) { "Test 1.4 failed" }
    check(part1(testInput) == 12) { "Part 1 main test failed" }


    // Part 2 Test Cases
    check(testInput[0].encodedCharCount() == 6) { "Test 2.1 failed" }
    check(testInput[1].encodedCharCount() == 9) { "Test 2.2 failed" }
    check(testInput[2].encodedCharCount() == 16) { "Test 2.3 failed" }
    check(testInput[3].encodedCharCount() == 11) { "Test 2.4 failed" }
    check(part2(testInput) == 19) { "Part 2 main test failed" }
}