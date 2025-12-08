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
    fun neighbors(): List<Vector> {
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

    fun getCell(location: Vector): Char? {
        val (x, y) = location
        return if (x !in 0 until width || y !in 0 until height) {
            null
        } else {
            grid[y][x]
        }
    }

    fun getNeighbors(location: Vector): List<Pair<Vector, Char?>> {
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
    val xRange = 0..input.first().lastIndex
    val yRange = 0..input.lastIndex

    fun isPaperRoll(x: Int, y: Int): Boolean {
        return x in xRange && y in yRange && input[y][x] == '@'
    }

    fun getOccupiedNeighbors(x: Int, y: Int): MutableList<Vector> {
        val result = mutableListOf<Vector>()

        for (dy in -1..1) {
            for (dx in -1..1) {
                if (((dy != 0) || (dx != 0)) && isPaperRoll(x + dx, y + dy)) result.add(Vector(x + dx, y + dy))
            }
        }

        return result
    }

    val queue = ArrayDeque<Vector>(5000)

    val adjacencyMatrix = buildMap {
        yRange.forEach { y ->
            xRange.forEach { x ->
                if (isPaperRoll(x, y)) {
                    val occupiedNeighbors = getOccupiedNeighbors(x, y)
                    put(Vector(x, y), occupiedNeighbors)
                    if (occupiedNeighbors.size < 4) {
                        queue.add(Vector(x, y))
                    }
                }
            }
        }
    }.toMutableMap()

    var count = 0

    while (queue.isNotEmpty()) {
        val location = queue.removeFirst()
        count += 1

        adjacencyMatrix.remove(location)?.forEach { loc ->
            val neighborsNeighbors = adjacencyMatrix.getValue(loc)
            neighborsNeighbors.remove(location)

            // Add to the work queue when it first satisfies the size condition
            if (neighborsNeighbors.size == 3) {
                queue.add(loc)
            }
        }
    }

    return count
}
