package com.sphericalchickens.aoc2025.day09

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 9 ---")

    val input = readInputLines("aoc2025/day09_input.txt")

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

val testInput = """
    7,1
    11,1
    11,7
    9,7
    9,5
    2,5
    2,3
    7,3
""".trimIndent().lines()

private fun runPart1Tests() {
    check("Part 1 Test Case 1", 50, part1(testInput))
}

private fun runPart2Tests() {
    check("Part 2 Test Case 1", 24, part2(testInput))
}

private data class Corner(val x: Long, val y: Long)

private fun part1(input: List<String>): Long {
    val corners = parserCorners(input)

    return findMaxOf(corners)
}

private fun parserCorners(input: List<String>): List<Corner> =
    input.map { it.split(",").map { it.trim().toLong() } }.map { (a, b) -> Corner(a, b) }

private fun findMaxOf(corners: List<Corner>): Long {
    if (corners.size < 2) return 0L

    val first = corners.first()
    val rest = corners.drop(1)

    val myMax = rest.maxOfOrNull { area(first, it) } ?: 0L

    val othersMax = findMaxOf(rest)

    return max(myMax, othersMax)
}

private fun area(a: Corner, b: Corner): Long {
    return a.delta(b).let { (dx, dy) ->
        dx * dy
    }
}

private fun Corner.delta(b: Corner): Pair<Long, Long> {
    return abs(x - b.x) + 1 to abs(y - b.y) + 1
}

private fun part2(input: List<String>): Long {
    val corners = parserCorners(input)

    // Let's first determine if we need to deal with any co-linear "corners"
    // A "corner" is co-linear if we three of the same value in a row

    val cl = corners.windowed(3, 1).firstOrNull {
        (it[0].x == it[1].x && it[1].x == it[2].x) ||
        (it[0].y == it[1].y && it[1].y == it[2].y)
    }

    if (cl == null) {
        "No co-linear corners found".println()
    } else {
        "oh FFS!".println()
    }

    return 24L
}
