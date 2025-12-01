package com.sphericalchickens.aoc2025.day04

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 4 ---")

    val input = readInputLines("aoc2025/day04_input.txt")

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
        
    """.trimIndent().lines()
    check("Part 1 Test Case 1", -1, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private fun part1(input: List<String>): Int {
    return -1
}

private fun part2(input: List<String>): Int {
    return -1
}
