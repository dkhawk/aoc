package com.sphericalchickens.aoc2025gem.day04

import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

fun main() {
    println("--- Advent of Code 2025, Day 4 (Gemini Refactor) ---")

    // Use relative path assuming project root
    val input = readInputLines("aoc2025/day04_input.txt")

    // --- Part 1 ---
    println("🧪 Running Part 1 tests...")
    runPart1Tests()
    println("✅ Part 1 tests passed!")

    println("🎁 Solving Part 1...")
    val (part1Result, part1Duration) = measureTimedValue {
        part1(input)
    }
    println("   Part 1: $part1Result")
    println("Part 1 runtime: $part1Duration")

    // --- Part 2 ---
    println("🧪 Running Part 2 tests...")
    runPart2Tests()
    println("✅ Part 2 tests passed!")

    println("🎀 Solving Part 2...")
    val (part2Result, part2Duration) = measureTimedValue {
        part2(input)
    }
    println("   Part 2: $part2Result")
    println("Part 2 runtime: $part2Duration")
}

// --- Domain Models ---

/**
 * A simple Point class.
 * We avoid adding a `neighbors()` method that returns a List to prevent allocation churn.
 */
private data class Point(val x: Int, val y: Int)

// --- Solutions ---

private fun part1(input: List<String>): Int {
    val height = input.size
    val width = input.first().length

    // We iterate through every cell in the grid.
    // Instead of creating Vector objects for every coordinate, we iterate primitives (x, y).
    var count = 0

    for (y in 0 until height) {
        val row = input[y]
        for (x in 0 until width) {
            // Only process if the current cell is active '@'
            if (row[x] == '@') {
                var activeNeighbors = 0
                
                // Check 8 neighbors manually to avoid object allocation.
                // This tight loop is significantly faster than generating lists of Vectors.
                for (dy in -1..1) {
                    for (dx in -1..1) {
                        if (dy == 0 && dx == 0) continue // Skip self

                        val ny = y + dy
                        val nx = x + dx

                        // Boundary checks and character check in one go
                        if (ny in 0 until height && nx in 0 until width && input[ny][nx] == '@') {
                            activeNeighbors++
                        }
                    }
                }

                // The condition: active cells with fewer than 4 active neighbors
                if (activeNeighbors < 4) {
                    count++
                }
            }
        }
    }
    return count
}

private fun part2(input: List<String>): Int {
    // 1. Parse the grid into a Set of active Points (Sparse representation)
    // using buildSet for a cleaner builder pattern.
    val activePoints = buildSet {
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                if (char == '@') add(Point(x, y))
            }
        }
    }

    // 2. Build the graph (Adjacency List)
    // We map each Point to its list of *active* neighbors.
    // This allows us to work purely in "Graph Space" rather than "Grid Space" for Part 2.
    val neighborsMap = HashMap<Point, MutableList<Point>>(activePoints.size)
    
    // We also track current degrees (count of active neighbors) to avoid re-counting.
    val degrees = HashMap<Point, Int>(activePoints.size)

    for (p in activePoints) {
        val nList = ArrayList<Point>(8)
        var count = 0
        
        // Generate neighbors logic inline
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val neighbor = Point(p.x + dx, p.y + dy)
                if (neighbor in activePoints) {
                    nList.add(neighbor)
                    count++
                }
            }
        }
        neighborsMap[p] = nList
        degrees[p] = count
    }

    // 3. Topology Peel / Queue Processing
    // Instead of scanning the whole grid repeatedly, we use a Queue.
    // Add all nodes that constitute a "violation" (degree < 4) to the removal queue.
    val removalQueue = ArrayDeque<Point>()
    degrees.forEach { (point, degree) ->
        if (degree < 4) removalQueue.add(point)
    }

    // Track removed points to ensure we don't double-process
    val removed = HashSet<Point>()
    
    while (removalQueue.isNotEmpty()) {
        val current = removalQueue.removeFirst()
        
        // If already processed, skip
        if (!removed.add(current)) continue

        // For every neighbor of the node we are removing...
        neighborsMap[current]?.forEach { neighbor ->
            // If the neighbor is still alive (not removed)
            if (neighbor !in removed) {
                // Decrement its degree (efficiently, O(1) lookup)
                val newDegree = (degrees[neighbor] ?: 0) - 1
                degrees[neighbor] = newDegree

                // If this neighbor effectively "died" because of the current removal
                // (it dropped from 4 to 3), add it to the queue to be processed.
                // We verify newDegree == 3 to ensure we only add it once, exactly when it crosses the threshold.
                // However, simpler logic is just checking if < 4 and not yet removed.
                if (newDegree < 4) {
                    removalQueue.add(neighbor)
                }
            }
        }
    }

    return removed.size
}

// --- Test Infrastructure ---

private fun check(name: String, expected: Int, actual: Int) {
    if (expected == actual) {
        // println("  ✅ $name passed") // Optional verbose logging
    } else {
        println("  ❌ $name FAILED: Expected $expected, but got $actual")
        throw AssertionError("Test failed")
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