package com.sphericalchickens.aoc2018.day17

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2018, Day 17: Reservoir Research
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    runTests2()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2018/day17_input.txt")
    println("\n--- Advent of Code 2018, Day 17: Reservoir Research ---")

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

fun part1(input: List<String>): Int {
    return solveWell(parseInput(input))
}

fun part2(input: List<String>): Int {
    return solveWell(parseInput(input), stillOnly = true)
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
        x=495, y=2..7
        y=7, x=495..501
        x=501, y=3..7
        x=498, y=2..4
        x=506, y=1..2
        x=498, y=10..13
        x=504, y=10..13
        y=13, x=498..504
    """.trimIndent().lines().filter(String::isNotBlank)

    val well = parseInput(testInput)

    // well.println()
    // TODO: Add real test expectations when implementing
    check("Parser", 13, well.maxY)

    check("solve well", 57, part1(testInput))
    //  check("Part 1 stub check", 0, part1(testInput))

    val testInput2 = """
        x=492, y=2..16
        x=499, y=5..10
        x=501, y=5..10
        x=508, y=3..16
        y=5, x=499..501
        y=10, x=499..501
        y=16, x=492..508
    """.trimIndent().lines().filter(String::isNotBlank)

    val well2 = parseInput(testInput2)

    // well.println()
    // TODO: Add real test expectations when implementing
    check("Parser", 16, well2.maxY)

    check("solve well2", 208, part1(testInput2))
    //  check("Part 1 stub check", 0, part1(testInput))

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
    """.trimIndent().lines().filter(String::isNotBlank)

    val well3 = parseInput(testInput3)

    // well.println()
    // TODO: Add real test expectations when implementing
    check("Parser", 55, well3.maxY)

    check("solve well2", 681, part1(testInput3))
}

fun runTests2() {
    val testInput = """
        x=495, y=2..7
        y=7, x=495..501
        x=501, y=3..7
        x=498, y=2..4
        x=506, y=1..2
        x=498, y=10..13
        x=504, y=10..13
        y=13, x=498..504
    """.trimIndent().lines().filter(String::isNotBlank)

    val well = parseInput(testInput)

    // well.println()
    // TODO: Add real test expectations when implementing
    check("Parser", 13, well.maxY)

    check("solve well", 29, part2(testInput))
}

interface Line {
    fun maxY(): Int
    fun toCoordinateCollection(): Collection<Vector>
}

data class Row(val y: Int, val xRange: IntRange) : Line {
    override fun maxY(): Int = y
    override fun toCoordinateCollection(): Collection<Vector> = xRange.map { x -> Vector(x, y) }
}

data class Col(val x: Int, val yRange: IntRange) : Line {
    override fun maxY(): Int = yRange.last
    override fun toCoordinateCollection(): Collection<Vector> = yRange.map { y -> Vector(x, y) }
}

fun parseInput(input: List<String>): Well {
    val lines = input.map { line ->
        val thing = line.trim().split(",").map { part ->
            part.trim().split("=").let { (dim, value) ->
                if (value.contains("..")) {
                    val r = value.split("..").let { (a, b) -> a.toInt()..b.toInt() }
                    dim to r
                } else {
                    dim to value.toInt()
                }
            }
        }

        val xThing = thing.first { it.first == "x" }.second
        val yThing = thing.first { it.first == "y" }.second

        if (xThing is Int && yThing is IntRange) {
            Col(xThing, yThing)
        } else if (yThing is Int && xThing is IntRange) {
            Row(yThing, xThing)
        } else {
            error("Invalid input: line $line")
        }
    }

    return Well(lines)
}

data class Vector(val x: Int, val y: Int) {
    operator fun plus(remove: Vector): Vector = Vector(x + remove.x, y + remove.y)
}

val DOWN = Vector(0, 1)
val UP = Vector(0, -1)
val LEFT = Vector(-1, 0)
val RIGHT = Vector(1, 0)

data class Well(val lines: List<Line>) {
    val maxY: Int by lazy {
        println("Calculating max...")
        lines.maxOf { line -> line.maxY() }
    }

    fun toSet(): Set<Vector> {
        return lines.flatMap { it.toCoordinateCollection() }.toSet()
    }
}

fun solveWell(well: Well, stillOnly: Boolean = false): Int {
    val occupied = well.toSet()

    // start with empty set
    val flowingWaterSet = mutableSetOf(Vector(x=500, y=0))
    val stillWaterSet = mutableSetOf<Vector>()

    val unresolvedHeads = mutableListOf(Vector(x=500, y=0))

//    printWell(occupied, flowingWaterSet, stillWaterSet, unresolvedHeads)
//    println()

    while (unresolvedHeads.isNotEmpty()) {
        val head = unresolvedHeads.removeFirst()
        flowingWaterSet.add(head)

        // What's right below us?
        val belowHead = head + DOWN

        if (belowHead.y > well.maxY) {
            // this head is down
            continue
        }

        val isBlocked = belowHead in occupied || belowHead in stillWaterSet

        if (!isBlocked) {
            unresolvedHeads.add(belowHead)
        } else {
            // Fill with still in both directions until we find a cliff or are blocked
            var leftHead = head
            var rightHead = head

            var leftIsBlocked = false
            var rightIsBlocked = false

            while (true) {
                // is left head over a drop?
                val belowLeft = leftHead + DOWN
                val leftOf = leftHead + LEFT

                val isBlockedBelow = belowLeft in occupied || belowLeft in stillWaterSet
                val isBlockedLeft = leftOf in occupied || leftOf in stillWaterSet

                if (isBlockedBelow && isBlockedLeft) {
                    leftIsBlocked = true
                    break
                } else if (!isBlockedBelow) {
                    // We can flow down!!
                    unresolvedHeads.add(belowLeft)
                    break
                } else {
                    flowingWaterSet.add(leftOf)
                    leftHead = leftOf
                }
            }

            while (true) {
                // is right head over a drop?
                val belowRight = rightHead + DOWN
                val rightOf = rightHead + RIGHT

                val isBlockedBelow = belowRight in occupied || belowRight in stillWaterSet
                val isBlockedRight = rightOf in occupied || rightOf in stillWaterSet

                if (isBlockedBelow && isBlockedRight) {
                    rightIsBlocked = true
                    break
                } else if (!isBlockedBelow) {
                    // We can flow down!!
                    unresolvedHeads.add(belowRight)
                    break
                } else {
                    flowingWaterSet.add(rightOf)
                    rightHead = rightOf
                }
            }

            if (leftIsBlocked && rightIsBlocked) {
                // Oof, this flow is blocked so search for the edge in both directions
                for (x in leftHead.x..rightHead.x) {
                    val v = Vector(x, leftHead.y)
                    stillWaterSet.add(v)
                    flowingWaterSet.remove(v)

                    // Also, convert all flowing water cells above this row to an unresolved head

                    if (leftHead.y > 0) {
                        val h = Vector(x, leftHead.y - 1)
                        if (h in flowingWaterSet) {
                            flowingWaterSet.remove(h)
                            unresolvedHeads.add(h)
                        }
                    }
                }

                val aboveHead = head + UP
                if (aboveHead in stillWaterSet || aboveHead in flowingWaterSet) {
                    unresolvedHeads.add(aboveHead)
                }
            }
        }
    }

//    println(printWell(occupied, flowingWaterSet, stillWaterSet, unresolvedHeads))

    val minY = occupied.minOf { it.y }
    val maxY = occupied.maxOf { it.y }

    val w = if (stillOnly) {
        (stillWaterSet).filter { it.y in minY..maxY }
    } else {
        (stillWaterSet + flowingWaterSet).filter { it.y in minY..maxY }
    }

    return w.size
}

fun printWell(
    occupied: Set<Vector>,
    flowingWaterSet: MutableSet<Vector>,
    stillWaterSet: MutableSet<Vector>,
    unresolvedHeads: MutableList<Vector>
): String {
    val output = StringBuilder()

    val minX = occupied.minBy { it.x }.x - 1
    val maxX = occupied.maxBy { it.x }.x + 1

    val minY = occupied.minBy { it.y }.y - 1
    val maxY = occupied.maxBy { it.y }.y + 1

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val v = Vector(x, y)
            var c = when (v) {
                in unresolvedHeads -> 'v'
                in flowingWaterSet -> '|'
                in stillWaterSet -> '~'
                in occupied -> '#'
                else -> '.'
            }

            if (c != '.' && v.y < (minY + 1)) {
                c = '+'
            }

            output.append(c)
        }
        output.append('\n')
    }

    return output.toString()
}
