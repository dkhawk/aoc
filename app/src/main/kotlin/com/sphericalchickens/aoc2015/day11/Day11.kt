package com.sphericalchickens.aoc2015.day11

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 11: Corporate Policy
 *
 * This program solves the puzzle for Day 11.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day11_input.txt")
    println("\n--- Advent of Code 2015, Day XX ---")


    // --- Part 1: TBD ---
    val part1Result = part1(puzzleInput.first())
    println("🎁 Part 1: $part1Result")


    // --- Part 2: TBD ---
    val part2Result = part1(part1Result)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: String): String {
    var next = input.next()
    while (true) {
        if (next.hasNoForbiddenLetters() && next.hasNonOverlappingPairs() && next.hasStraight()) {
            return next
        }
        next = next.next()
    }
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
        hijklmmn
        abbceffg
        abbcegjk
        abcdffaa
        ghjaabcc
    """.trimIndent().lines()

    check("hijklmmn".hasStraight())
    check(!"hijklmmn".hasNoForbiddenLetters())
    check("abbceffg".hasNoForbiddenLetters())

    check("abcdffaa".hasNonOverlappingPairs())
    check(!"aaadfgab".hasNonOverlappingPairs())

    // xx, xy, xz, ya, yb
    check("xx".next() == "xy")
    check("xy".next() == "xz")
    check("xz".next() == "ya")
    check("ya".next() == "yb")
    check("yzzz".next() == "zaaa")

    check(part1("abcdefgh") == "abcdffaa")
}

private fun String.next() : String {
    var carry = 1
    var result = mutableListOf<Char>()
    var i = lastIndex
    while (i >= 0) {
        val next = this[i] + carry
        if (next > 'z') {
            result.add('a')
        } else {
            carry = 0
            result.add(next)
        }
        i--
    }
    return result.reversed().joinToString(separator = "")
}

private fun String.hasNonOverlappingPairs(): Boolean {
    var pairCount = 0
    var i = 0
    while (i < this.lastIndex) {
        if (this[i] == this[i + 1]) {
            pairCount++
            if (pairCount == 2) return true

            // Add one to prevent overlaps from matching
            i++
        }
        i++
    }

    return false
}

private fun String.hasStraight(): Boolean {
    return windowed(3, 1, false).any { it[1] - it[0] == 1 && it[2] - it[1] == 1}
}

private fun String.hasNoForbiddenLetters() : Boolean {
    val forbiddenLetters = "iol".toList()
    return this.none { it in forbiddenLetters }
}