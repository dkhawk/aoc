package com.sphericalchickens.aoc2016gem.day09

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

/*
 * --- Advent of Code 2016, Day 09 ---
 *
 * We're refactoring the original solution to be more efficient and idiomatic.
 * The primary change is moving from a CharIterator-based approach to an
 * index-based approach. This allows us to make recursive calls in Part 2
 * without allocating new substrings, which is a significant performance bottleneck
 * in the original code. We "slice" the string by passing (start, end) indices.
 */

// We'll keep your excellent main function and test harnesses exactly as they are.
// They provide a great way to control the workflow and validate our changes.
fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 09 ---")

    val input = readInputLines("aoc2016/day09_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            // We join the lines, as the problem treats whitespace as data.
            // The original did this too, which is correct.
            val part1Result = part1(input.joinToString(separator = ""))
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input.joinToString(separator = ""))
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

// All test functions are identical to the original and are kept for validation.
private fun runPart1Tests() {
    val testInput = """
        ADVENT
        A(1x5)BC
        (3x3)XYZ
        A(2x2)BCD(2x2)EFG
        (6x1)(1x3)A
        X(8x2)(3x3)ABCY
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 6, part1(testInput[0]))
    check("Part 1 Test Case 2", 7, part1(testInput[1]))
    check("Part 1 Test Case 3", 9, part1(testInput[2]))
    check("Part 1 Test Case 4", 11, part1(testInput[3]))
    check("Part 1 Test Case 5", 6, part1(testInput[4]))
    check("Part 1 Test Case 6", 18, part1(testInput[5]))
}

private fun runPart2Tests() {
    val testInput = """
        (3x3)XYZ
        X(8x2)(3x3)ABCY
        (27x12)(20x12)(13x14)(7x10)(1x12)A
        (25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "XYZXYZXYZ".length.toLong(), part2(testInput[0]))
    check("Part 2 Test Case 2", "XABCABCABCABCABCABCY".length.toLong(), part2(testInput[1]))
    check("Part 2 Test Case 3", 241920, part2(testInput[2]))
    check("Part 2 Test Case 4", 445, part2(testInput[3]))
}

// --- Refactored Solution ---

/**
 * To support our DRY (Don't Repeat Yourself) goal, we first create a helper
 * `data class`. This class will hold the results of parsing a marker.
 * Using a `data class` is idiomatic Kotlin. It gives us `equals()`,
 * `hashCode()`, `toString()`, and most importantly, `componentN()` functions,
 * which allow for destructuring declarations.
 */
private data class MarkerParseResult(
    val charCount: Int,
    val multiplier: Int,
    val markerEndIndex: Int // The index *after* the closing ')'
)

/**
 * This is our extracted helper function. It replaces the duplicated
 * `buildString` logic from the original `part1` and `part2`.
 *
 * It takes the full input string and the `startIndex` where a '(' was found.
 * It uses efficient `indexOf` calls to find the 'x' and ')' delimiters.
 * It then parses the numbers in between and returns our `MarkerParseResult`
 * containing all the information we need.
 *
 * Note: This function assumes `s[startIndex] == '('` and that the marker
 * is well-formed. This is a safe assumption for AoC puzzles.
 */
private fun parseMarker(s: String, startIndex: Int): MarkerParseResult {
    // Find the delimiters
    val xIndex = s.indexOf('x', startIndex + 1)
    val closeParenIndex = s.indexOf(')', xIndex + 1)

    // Parse the numbers. .substring() is efficient here as it just
    // creates a new String header, not a full copy (on modern JVMs
    // for small strings, or a view). Even if it copies, these are
    // tiny strings (e.g., "27", "12").
    val charCount = s.substring(startIndex + 1, xIndex).toInt()
    val multiplier = s.substring(xIndex + 1, closeParenIndex).toInt()

    return MarkerParseResult(charCount, multiplier, closeParenIndex + 1)
}

/**
 * --- Part 1 Refactored ---
 *
 * We now iterate using an index `i` instead of an iterator.
 * This makes it trivial to jump forward.
 */
private fun part1(input: String): Long {
    var count = 0L
    var i = 0 // Our new index
    while (i < input.length) {
        if (input[i] == '(') {
            // We found a marker. Call our helper.
            val (charCount, multiplier, markerEndIndex) = parseMarker(input, i)
            
            // This is the "destructuring declaration" I mentioned.
            // It's much cleaner than `val result = parseMarker...`
            // and then `result.charCount`, etc.

            // Part 1 logic: add the decompressed length.
            // We use .toLong() for safety to avoid Int overflow,
            // though with these multipliers it's fine.
            count += charCount.toLong() * multiplier

            // Jump our index `i` past the marker AND the data segment.
            i = markerEndIndex + charCount
        } else {
            // It's a normal character.
            count++
            i++
        }
    }
    return count
}

/**
 * --- Part 2 Refactored ---
 *
 * The `part2` function becomes a simple "entry point" or "wrapper"
 * that kicks off our recursive helper function.
 * It tells the helper to process the *entire* string,
 * from index 0 to `input.length`.
 */
private fun part2(input: String): Long {
    return getDecompressedLength(input, 0, input.length)
}

/**
 * This is the core of the Part 2 refactor. This recursive function
 * calculates the decompressed length of a *segment* of the string `s`,
 * defined by the half-open range `[start, end)`.
 *
 * It avoids all `substring` allocations for recursion.
 */
private fun getDecompressedLength(s: String, start: Int, end: Int): Long {
    var count = 0L
    var i = start
    while (i < end) {
        if (s[i] == '(') {
            // Found a marker, parse it.
            val (charCount, multiplier, markerEndIndex) = parseMarker(s, i)

            // Define the segment this marker applies to.
            // It starts right after the marker...
            val segmentStart = markerEndIndex
            // ...and ends after `charCount` characters.
            val segmentEnd = segmentStart + charCount

            // --- This is the crucial part ---
            // We recursively call ourself to get the *decompressed length*
            // of the segment this marker applies to.
            // We pass the *same* string `s` but new indices.
            val subLength = getDecompressedLength(s, segmentStart, segmentEnd)

            // The total length for this block is the segment's
            // decompressed length, times the multiplier.
            count += subLength * multiplier.toLong()

            // We then jump our index `i` to the end of the segment,
            // skipping over the characters we just processed recursively.
            i = segmentEnd
        } else {
            // It's a normal character.
            count++
            i++
        }
    }
    return count
}