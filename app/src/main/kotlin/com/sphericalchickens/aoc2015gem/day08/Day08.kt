package com.sphericalchickens.aoc2015gem.day08

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 8: Matchsticks
 *
 * This program solves a puzzle about the difference between the number of characters
 * in a string's code representation (the raw string literal) and the number of
 * characters in its in-memory value after parsing escape sequences.
 *
 * ## Principles Applied
 * 1.  **Literate Programming**: The `main` function is a narrative, explaining each step
 * from testing to solving both parts of the puzzle. Comments describe the *why* of
 * each step.
 * 2.  **Idiomatic Kotlin**:
 * - **Extension Properties**: Instead of standalone `unescape` and `escape` functions
 * that return a new `String`, we use `String.memoryLength` and `String.encodedLength`
 * extension properties. This is more efficient as it calculates the length directly
 * without allocating a new string. It also makes the call site read more naturally
 * (e.g., `line.memoryLength`).
 * - **Aggregate Functions**: `sumOf` is used to cleanly and expressively sum up the
 * calculated differences for each line, avoiding manual loops.
 * - **Direct Calculation**: The logic avoids creating intermediate strings, leading to
 * better performance and less memory pressure.
 */
fun main() {
    // --- Verification ---
    // We start by running checks against the known examples from the puzzle description.
    // This ensures our logic is sound before applying it to the real puzzle input.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // Read the puzzle input file, which contains a list of string literals.
    val puzzleInput = readInputLines("aoc2015/day08_input.txt")
    println("\n--- Advent of Code 2015, Day 8: Matchsticks ---")


    // --- Part 1: Difference between code and memory length ---
    // We calculate the total number of characters in the raw file minus the total number
    // of characters the strings would occupy in memory after parsing escape sequences.
    val part1Result = puzzleInput.sumOf { it.length - it.memoryLength }
    println("🎁 Part 1: The difference is $part1Result characters.")


    // --- Part 2: Difference between new encoded and original code length ---
    // We calculate the total number of characters required to re-encode each string literal,
    // then subtract the original number of characters to find the difference.
    val part2Result = puzzleInput.sumOf { it.encodedLength - it.length }
    println("🎀 Part 2: The new difference is $part2Result characters.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic: Extension Properties
// ---------------------------------------------------------------------------------------------

/**
 * ## Calculates the in-memory character length of a string literal.
 *
 * This function parses the string, accounting for escape sequences, without creating a
 * new string object. It iterates through the characters between the outer quotes.
 * - A normal character adds 1 to the length.
 * - An escape sequence like `\\` or `\"` adds 1 to the length but consumes 2 characters.
 * - A hex escape like `\x27` adds 1 to the length but consumes 4 characters.
 */
private val String.memoryLength: Int
    get() {
        // Start by considering the content between the two outer quotes.
        val content = this.removeSurrounding("\"")
        var length = 0
        var i = 0
        while (i < content.length) {
            i += if (content[i] == '\\') {
                // Check for hex escape sequence `\xHH`.
                if (i + 1 < content.length && content[i + 1] == 'x') {
                    4 // Skip `\`, `x`, and two hex digits.
                } else {
                    2 // Skip `\` and the character it escapes (e.g., `"` or `\`).
                }
            } else {
                1 // A regular character.
            }
            length += 1 // Each parsed group (char or escape sequence) is one character in memory.
        }
        return length
    }

/**
 * ## Calculates the encoded length of a string literal.
 *
 * To encode the string, we must:
 * 1. Add surrounding quotes (2 characters).
 * 2. Escape each existing backslash (`\`), adding 1 character per instance.
 * 3. Escape each existing quote (`"`), adding 1 character per instance.
 * The final length is the original length plus these additions.
 */
private val String.encodedLength: Int
    get() {
        // The new length is the original length + 2 for the new outer quotes
        // + 1 for each character that needs to be escaped.
        val charsToEscape = this.count { it == '"' || it == '\\' }
        return this.length + charsToEscape + 2
    }


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput1 = listOf(
        "\"\"",
        "\"abc\"",
        "\"aaa\\\"aaa\"",
        "\"\\x27\""
    )

    // Part 1 Test Cases: memory length
    check(testInput1[0].memoryLength == 0)
    check(testInput1[1].memoryLength == 3)
    check(testInput1[2].memoryLength == 7)
    check(testInput1[3].memoryLength == 1)

    // Part 2 Test Cases: encoded length
    check(testInput1[0].encodedLength == 6)
    check(testInput1[1].encodedLength == 9)
    check(testInput1[2].encodedLength == 16)
    check(testInput1[3].encodedLength == 11)
}