package com.sphericalchickens.aoc2025.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputText
import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 5 ---")

    val input = readInputText("aoc2025/day05_input.txt").lines()

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

private fun runPart1Tests() {
    val testInput = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent().lines()

    check("Part 1 Test Case 1", 3, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent().lines()
    check("Part 2 Test Case 1", 14, part2(testInput))
}

private fun part1(input: List<String>): Int {
    val recipes = input.takeWhile { it.isNotBlank() }.map { it.split("-").map(String::toLong) }.map { it.first()..it.last() }
    val ingredients = input.drop(recipes.size + 1).filter { it.isNotBlank() }.map { it.trim().toLong() }

    return ingredients.count { ingredient -> recipes.any { recipe -> ingredient in recipe } }
}

private fun part2(input: List<String>): Long {
    val recipes = input.takeWhile { it.isNotBlank() }.map { it.split("-").map(String::toLong) }.map { it.first()..it.last() }

    val sorted = recipes.sortedBy { it.first }

    val iter = sorted.iterator()

    val mergedRanges = buildList {
        var current = iter.next()

        iter.forEachRemaining { next ->
            if (next.first > current.last) {
                add(current)
                current = next
            } else {
                val last = max(current.last, next.last)
                current = current.first..last
            }
        }

        add(current)
    }

    return mergedRanges.sumOf { (it.last - it.first) + 1 }
}
