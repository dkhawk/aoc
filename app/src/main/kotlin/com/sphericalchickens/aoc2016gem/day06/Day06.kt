package com.sphericalchickens.aoc2016gem.day06

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 06 ---")

    val input = readInputLines("aoc2016/day06_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input)
            // The original solution produced 'cyxeoccr'
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
            val part2Result = part2(input)
            // The original solution produced 'batvzmms'
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        eedadn
        drvtee
        eandsr
        raavrd
        atevrs
        tsrnev
        sdttsa
        rasrtv
        nssdts
        ntnada
        svetve
        tesnvt
        vntsnd
        vrdear
        dvrsen
        enarar
    """.trimIndent().lines()
    check("Part 1 Test Case 1", "easter", part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        eedadn
        drvtee
        eandsr
        raavrd
        atevrs
        tsrnev
        sdttsa
        rasrtv
        nssdts
        ntnada
        svetve
        tesnvt
        vntsnd
        vrdear
        dvrsen
        enarar
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "advent", part2(testInput))
}

private fun solve(input: List<String>, selector: (Map<Char, Int>) -> Char): String {
    if (input.isEmpty()) return ""

    val messageLength = input.first().length

    // Iterate through each column index of the message.
    return (0 until messageLength).map { colIndex ->
        // For the current column, create a frequency map of its characters.
        // `groupingBy` followed by `eachCount` is the idiomatic and efficient
        // way to build a frequency map in Kotlin. It avoids creating
        // intermediate collections.
        val counts = input.groupingBy { line -> line[colIndex] }.eachCount()

        // Apply the provided selector logic (e.g., find max or min) to the
        // frequency map to determine the character for this column.
        selector(counts)
    }.joinToString("")
}

private fun part1(input: List<String>): String {
    // To solve Part 1, we provide a selector that finds the entry
    // with the maximum count (`it.value`) and returns its key (`.key`).
    return solve(input) { counts ->
        counts.maxByOrNull { it.value }!!.key
    }
}

private fun part2(input: List<String>): String {
    // For Part 2, the logic is identical, but we use a selector
    // that finds the entry with the minimum count.
    return solve(input) { counts ->
        counts.minByOrNull { it.value }!!.key
    }
}
