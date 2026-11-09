package com.sphericalchickens.aoc2018gem.day17

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2018, Day 17: Reservoir Research (Refactored)
 *
 * This solution simulates water flowing downward from a spring at `(500, 0)` through a 2D vertical
 * cross-section of sand and clay veins.
 *
 * ## Literate Walkthrough
 *
 * ### 1. Modeling the Domain
 * Rather than maintaining separate `Row` and `Col` classes with runtime type checks, we recognize
 * that every input line expands into a set of fixed clay coordinates. A [Reservoir] holds this
 * immutable `Set<Vector>` along with its precomputed vertical and horizontal bounding box.
 *
 * ### 2. The Water Simulation Algorithm
 * Water moves in two alternating phases:
 * - **Falling (`DOWN`):** If the tile directly beneath a water head is neither clay (`#`) nor
 *   settled water (`~`), the water continues falling downward until it either exceeds `maxY`
 *   (falling into the abyss) or lands on a solid surface.
 * - **Spreading (`LEFT` and `RIGHT`):** Once supported from below, water spreads horizontally in
 *   both directions as long as there is solid support underneath. Each horizontal scan terminates
 *   in one of two ways, modeled by the sealed interface [SpreadResult]:
 *   1. [SpreadResult.Blocked]: It hits a clay wall (`#`).
 *   2. [SpreadResult.Spillover]: The solid floor beneath it ends, creating a drop-off where water
 *      falls downward again.
 *
 * - **Settling (`~`) vs. Flowing (`|`):**
 *   If *both* the left and right scans hit walls ([SpreadResult.Blocked]), the entire horizontal
 *   span becomes settled water (`~`). Because this newly settled layer now acts as a solid floor
 *   for the row above it, any flowing water tiles (`|`) immediately above this span are re-queued
 *   to re-evaluate their horizontal spread.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2018/day17_input.txt")
    println("\n--- Advent of Code 2018, Day 17: Reservoir Research (Refactored) ---")

    // --- Part 1 ---
    val (part1Result, part1Duration) = measureTimedValue {
        part1(puzzleInput)
    }
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${formatDuration(part1Duration)}")

    // --- Part 2 ---
    val (part2Result, part2Duration) = measureTimedValue {
        part2(puzzleInput)
    }
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${formatDuration(part2Duration)}")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int =
    simulateWater(parseInput(input)).totalReachable

fun part2(input: List<String>): Int =
    simulateWater(parseInput(input)).settledWater

// ---------------------------------------------------------------------------------------------
// Domain Types & Parsing
// ---------------------------------------------------------------------------------------------

data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector): Vector = Vector(x + other.x, y + other.y)

    companion object {
        val SPRING = Vector(500, 0)
        val DOWN = Vector(0, 1)
        val UP = Vector(0, -1)
        val LEFT = Vector(-1, 0)
        val RIGHT = Vector(1, 0)
    }
}

data class Reservoir(val clay: Set<Vector>) {
    val minY: Int = clay.minOf { it.y }
    val maxY: Int = clay.maxOf { it.y }
    val minX: Int = clay.minOf { it.x }
    val maxX: Int = clay.maxOf { it.x }
}

private val LINE_REGEX = """([xy])=(\d+), [xy]=(\d+)\.\.(\d+)""".toRegex()

/**
 * Parses vein definitions such as `x=495, y=2..7` or `y=7, x=495..501` directly into a [Reservoir].
 */
fun parseInput(input: List<String>): Reservoir {
    val clay = buildSet {
        for (line in input) {
            if (line.isBlank()) continue
            val (firstAxis, fixedStr, rangeStartStr, rangeEndStr) = LINE_REGEX
                .matchEntire(line.trim())
                ?.destructured
                ?: error("Invalid input line: $line")

            val fixed = fixedStr.toInt()
            val range = rangeStartStr.toInt()..rangeEndStr.toInt()

            if (firstAxis == "x") {
                for (y in range) add(Vector(fixed, y))
            } else {
                for (x in range) add(Vector(x, fixed))
            }
        }
    }
    return Reservoir(clay)
}

// ---------------------------------------------------------------------------------------------
// Simulation Engine
// ---------------------------------------------------------------------------------------------

/**
 * Represents the outcome of scanning horizontally across a surface in a single direction.
 */
private sealed interface SpreadResult {
    val end: Vector

    /** Water hit a solid clay wall at `end + direction`. */
    data class Blocked(override val end: Vector) : SpreadResult

    /** Water reached an unsupported ledge at `end` and can fall into `dropBelow`. */
    data class Spillover(override val end: Vector, val dropBelow: Vector) : SpreadResult
}

data class SimulationResult(
    val totalReachable: Int,
    val settledWater: Int,
    val flowing: Set<Vector>,
    val settled: Set<Vector>
)

fun simulateWater(reservoir: Reservoir): SimulationResult {
    val clay = reservoir.clay
    val flowing = mutableSetOf<Vector>()
    val settled = mutableSetOf<Vector>()
    val queue = ArrayDeque<Vector>().apply { add(Vector.SPRING) }

    fun isSolid(pos: Vector): Boolean = pos in clay || pos in settled

    /**
     * Scans horizontally from [start] in [direction] (`LEFT` or `RIGHT`), marking visited
     * tiles as flowing water until either a wall blocks movement or the floor drops away.
     */
    fun spread(start: Vector, direction: Vector): SpreadResult {
        var current = start
        while (true) {
            flowing.add(current)
            val below = current + Vector.DOWN
            if (!isSolid(below)) {
                return SpreadResult.Spillover(end = current, dropBelow = below)
            }
            val next = current + direction
            if (isSolid(next)) {
                return SpreadResult.Blocked(end = current)
            }
            current = next
        }
    }

    while (queue.isNotEmpty()) {
        val head = queue.removeFirst()

        // Skip if a previous iteration already settled this cell
        if (head in settled) continue

        flowing.add(head)
        val below = head + Vector.DOWN

        // Stop tracking water that falls past the lowest clay vein
        if (below.y > reservoir.maxY) continue

        // Case 1: Nothing solid beneath us -> fall straight down
        if (!isSolid(below)) {
            if (below !in flowing) {
                flowing.add(below)
                queue.add(below)
            }
            continue
        }

        // Case 2: Supported from below -> spread horizontally left and right
        val leftResult = spread(head, Vector.LEFT)
        val rightResult = spread(head, Vector.RIGHT)

        if (leftResult is SpreadResult.Blocked && rightResult is SpreadResult.Blocked) {
            // Bounded by walls on both sides: convert this horizontal span into settled water
            for (x in leftResult.end.x..rightResult.end.x) {
                val cell = Vector(x, head.y)
                settled.add(cell)
                flowing.remove(cell)

                // Any flowing water immediately above this newly settled layer now has solid
                // support beneath it and must be re-evaluated.
                val above = cell + Vector.UP
                if (above in flowing) {
                    queue.add(above)
                }
            }
        } else {
            // Water spills over on at least one side
            if (leftResult is SpreadResult.Spillover && leftResult.dropBelow !in flowing) {
                flowing.add(leftResult.dropBelow)
                queue.add(leftResult.dropBelow)
            }
            if (rightResult is SpreadResult.Spillover && rightResult.dropBelow !in flowing) {
                flowing.add(rightResult.dropBelow)
                queue.add(rightResult.dropBelow)
            }
        }
    }

    val validYRange = reservoir.minY..reservoir.maxY
    val settledCount = settled.count { it.y in validYRange }
    val flowingCount = flowing.count { it.y in validYRange && it !in settled }

    return SimulationResult(
        totalReachable = settledCount + flowingCount,
        settledWater = settledCount,
        flowing = flowing,
        settled = settled
    )
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

fun Reservoir.render(
    flowing: Set<Vector>,
    settled: Set<Vector>,
    activeHeads: Collection<Vector> = emptySet()
): String = buildString {
    val activeSet = activeHeads.toSet()
    for (y in (minY - 1)..(maxY + 1)) {
        for (x in (minX - 1)..(maxX + 1)) {
            val pos = Vector(x, y)
            val ch = when {
                pos == Vector.SPRING -> '+'
                pos in activeSet -> 'v'
                pos in settled -> '~'
                pos in flowing -> '|'
                pos in clay -> '#'
                else -> '.'
            }
            append(ch)
        }
        appendLine()
    }
}

private fun runTests() {
    val testInput1 = """
        x=495, y=2..7
        y=7, x=495..501
        x=501, y=3..7
        x=498, y=2..4
        x=506, y=1..2
        x=498, y=10..13
        x=504, y=10..13
        y=13, x=498..504
    """.trimIndent().lines()

    val reservoir1 = parseInput(testInput1)
    check("Parser maxY (test 1)", 13, reservoir1.maxY)
    val result1 = simulateWater(reservoir1)
    check("Part 1 (test 1)", 57, result1.totalReachable)
    check("Part 2 (test 1)", 29, result1.settledWater)

    val testInput2 = """
        x=492, y=2..16
        x=499, y=5..10
        x=501, y=5..10
        x=508, y=3..16
        y=5, x=499..501
        y=10, x=499..501
        y=16, x=492..508
    """.trimIndent().lines()

    val reservoir2 = parseInput(testInput2)
    check("Parser maxY (test 2)", 16, reservoir2.maxY)
    check("Part 1 (test 2)", 208, part1(testInput2))

    val testInput3 = """
        x=488, y=27..40
        x=490, y=4..18
        x=497, y=25..34
        x=498, y=7..12
        x=499, y=25..34
        x=500, y=46..55
        x=501, y=7..12
        x=505, y=28..40
        x=507, y=50..52
        x=509, y=4..18
        x=511, y=50..52
        x=512, y=11..32
        x=521, y=45..55
        x=536, y=10..32
        y=12, x=498..501
        y=18, x=490..509
        y=32, x=512..536
        y=34, x=497..499
        y=40, x=488..505
        y=50, x=507..511
        y=52, x=507..511
        y=55, x=500..521
    """.trimIndent().lines()

    val reservoir3 = parseInput(testInput3)
    check("Parser maxY (test 3)", 55, reservoir3.maxY)
    check("Part 1 (test 3)", 681, part1(testInput3))
}
