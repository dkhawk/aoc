package com.sphericalchickens.aoc2025.day04

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
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
        ..@@.@@@@.
        @@@.@.@.@@
        @@@@@.@.@@
        @.@@@@..@.
        @@.@@@@.@@
        .@@@@@@@.@
        .@.@.@.@@@
        @.@@@.@@@@
        .@@@@@@@@.
        @.@.@@@.@.
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 13, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        ..@@.@@@@.
        @@@.@.@.@@
        @@@@@.@.@@
        @.@@@@..@.
        @@.@@@@.@@
        .@@@@@@@.@
        .@.@.@.@@@
        @.@@@.@@@@
        .@@@@@@@@.
        @.@.@@@.@.        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", 43, part2(testInput))
}

private data class Vector(val x: Int, val y: Int) {
    fun neighbors() : List<Vector> {
        return listOf(
            Vector(x - 1, y - 1),
            Vector(x + 0, y - 1),
            Vector(x + 1, y - 1),
            Vector(x - 1, y + 0),
            Vector(x + 1, y + 0),
            Vector(x - 1, y + 1),
            Vector(x + 0, y + 1),
            Vector(x + 1, y + 1),
        )
    }
}

private fun part1(input: List<String>): Int {
    val grid = input
    val width = input.first().length
    val height = input.size

    fun getCell(location: Vector) : Char? {
        val (x, y) = location
        return if (x !in 0 until width || y !in 0 until height) {
            null
        } else {
            grid[y][x]
        }
    }

    fun getNeighbors(location: Vector) : List<Pair<Vector, Char?>> {
        return location.neighbors().map { it to getCell(it) }
    }

    return (0 until height).sumOf { y ->
        (0 until width).sumOf { x ->
            val location = Vector(x, y)
            if (getCell(location) == '@') {
                if (getNeighbors(location).count { it.second == '@' } < 4) 1 else 0
            } else {
                0
            }
        }
    }
}

private fun part2(input: List<String>): Int {
    val grid = input.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '@') Vector(x, y) else null
        }
    }.flatten().toSet().toMutableSet()

    val start = grid.size

    fun getNeighbors(location: Vector) : List<Vector> {
        return location.neighbors().filter { it in grid }
    }

    while (true) {
        val candidates = grid.filter { location ->
            getNeighbors(location).size < 4
        }.toSet()

        if (candidates.isEmpty()) {
            break
        } else {
            grid.removeAll(candidates)
        }
    }

    return start - grid.size
}
