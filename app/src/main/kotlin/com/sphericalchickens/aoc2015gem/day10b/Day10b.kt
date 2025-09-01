package com.sphericalchickens.aoc2015gem.day10b

import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureNanoTime

fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day10_input.txt")
    println("\n--- Advent of Code 2015, Day 10 ---")


    // --- Part 1: TBD ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")


    // --- Part 2: TBD ---
    val part2Result = part2(puzzleInput)
    println("🎀 Part 2: $part2Result")

    // --- Benchmarking ---
    println("\n--- Benchmarking (50 iterations) ---")
    println("Running each implementation to see how long it takes...")

    benchmarkImplementation("Indexed Loop", puzzleInput.first(), ::lookAndSay)
    benchmarkImplementation("Regex", puzzleInput.first(), ::lookAndSayRegex)
    benchmarkImplementation("Grouping", puzzleInput.first(), ::lookAndSayGrouping)
}

// ---------------------------------------------------------------------------------------------
// Core Logic & Solutions
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {
    return applyLookAndSay(input.first(), 40, ::lookAndSay)
}

fun part2(input: List<String>): Int {
    return applyLookAndSay(input.first(), 50, ::lookAndSay)
}

private fun applyLookAndSay(
    initialString: String,
    iterations: Int,
    lookAndSayFn: (String) -> String
): Int {
    val finalString = generateSequence(initialString, lookAndSayFn)
        .drop(iterations)
        .first()

    return finalString.length
}

// ---------------------------------------------------------------------------------------------
// Look-and-Say Implementations
// ---------------------------------------------------------------------------------------------

/**
 * Implementation 1: Simple indexed while-loop. Generally the most performant.
 */
private fun lookAndSay(input: String): String {
    return buildString {
        var i = 0
        while (i < input.length) {
            val char = input[i]
            var count = 1
            while (i + count < input.length && input[i + count] == char) {
                count++
            }
            append(count)
            append(char)
            i += count
        }
    }
}

/**
 * Implementation 2: Regular expressions. Concise but has performance overhead.
 */
private fun lookAndSayRegex(input: String): String {
    val regex = """(.)\1*""".toRegex()
    return regex.findAll(input).joinToString("") { matchResult ->
        val group = matchResult.value
        "${group.length}${group.first()}"
    }
}

/**
 * Implementation 3: Functional grouping. Idiomatic but can have overhead from
 * intermediate object creation (sequences and lists).
 */
private fun lookAndSayGrouping(input: String): String {
    return input.asSequence()
        .groupConsecutive()
        .map { group -> "${group.size}${group.first()}" }
        .joinToString("")
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Groups consecutive equal elements of a sequence into lists.
 */
fun <T> Sequence<T>.groupConsecutive(): Sequence<List<T>> {
    return sequence {
        val iterator = this@groupConsecutive.iterator()
        if (!iterator.hasNext()) return@sequence

        var currentGroup = mutableListOf(iterator.next())
        for (element in iterator) {
            if (element == currentGroup.first()) {
                currentGroup.add(element)
            } else {
                yield(currentGroup)
                currentGroup = mutableListOf(element)
            }
        }
        yield(currentGroup)
    }
}

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testCases = mapOf(
        "1" to "11",
        "11" to "21",
        "21" to "1211",
        "1211" to "111221",
        "111221" to "312211"
    )

    testCases.forEach { (input, expected) ->
        check(lookAndSay(input) == expected)
        check(lookAndSayRegex(input) == expected)
        check(lookAndSayGrouping(input) == expected)
    }
}

/**
 * A simple benchmarking utility to measure and print execution time.
 */
private fun benchmarkImplementation(
    name: String,
    initialString: String,
    implementation: (String) -> String
) {
    var result = ""
    val elapsedNanos = measureNanoTime {
        result = generateSequence(initialString, implementation).drop(50).first()
    }
    // Format to milliseconds with a few decimal places for readability
    val elapsedMillis = elapsedNanos / 1_000_000.0
    println(
        "  %-12s -> Final Length: %-10d | Time: %.3f ms".format(
            "$name:",
            result.length,
            elapsedMillis
        )
    )
}