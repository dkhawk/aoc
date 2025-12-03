package com.sphericalchickens.aoc2025.day03

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 3 ---")

    val input = readInputLines("aoc2025/day03_input.txt")

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
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        987654321111111
        811111111111119
        234234234234278
        818181911112111
    """.trimIndent().lines()

    val expected = listOf(98, 89, 78, 92,)

    testInput.zip(expected).forEach { (line, expected) ->
        val a = maxJoltage(line)
        check(line, expected, a)
    }

    check("Part 1 Test Case 1", 357, part1(testInput))
}

private fun maxJoltage(line: String): Int {
    val max = line.dropLast(1).max()
    val max2 = line.substringAfter(max).max()

    val a = ((max - '0') * 10) + (max2 - '0')
    return a
}

private fun maxJoltage12(line: String): Long {
    var justAfterPrevious = 0

    val digits = (11 downTo 0).map { i ->
        val max = line.drop(justAfterPrevious).dropLast(i).max()
        justAfterPrevious += line.drop(justAfterPrevious).indexOf(max) + 1
        max
    }

    return digits.joinToString("").toLong()
}

private fun runPart2Tests() {
    val testInput = """
        987654321111111
        811111111111119
        234234234234278
        818181911112111
    """.trimIndent().lines()

    val expected = listOf(987654321111, 811111111119, 434234234278, 888911112111)

    testInput.zip(expected).forEach { (line, expected) ->
        val a = maxJoltage12(line)
        check(line, expected, a)
    }

    check("Part 2 Test Case 1", 3121910778619, part2(testInput))
}

private fun part1(input: List<String>): Int {
    return input.sumOf { maxJoltage(it) }
}

private fun part2(input: List<String>): Long {
    return input.sumOf { maxJoltage12(it) }
}
