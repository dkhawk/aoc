package com.sphericalchickens.aoc2016.day15

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 15 ---")

    val input = readInputLines("aoc2016/day15_input.txt")

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
        Disc #1 has 5 positions; at time=0, it is at position 4.
        Disc #2 has 2 positions; at time=0, it is at position 1.
    """.trimIndent().lines()
    check("Part 1 Test Case 1", Disc(1, 5, 4), testInput.first().toDisc())
    check("Part 1 Test Case 2", Disc(2, 2, 1), testInput[1].toDisc())
    check("Part 1 Test Case 1", 5, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun part1(input: List<String>): Int {
    val discs = input.map { line -> line.toDisc() }.sortedByDescending { it.positions }
    return solve(discs)
}

private fun Disc.isSolution(time: Int): Boolean {
    val diff = time - firstAlignment
    return diff.mod(positions) == 0
}

private data class Disc(
    val id: Int,
    val positions: Int,
    val initialPosition: Int,
) {
    val firstAlignment = run {
        val x = positions - (id + initialPosition)
        if (x < 0) {
            x + positions
        } else {
            x
        }
    }
}

private val discRex = Regex("""Disc #(?<id>\d+) has (?<positions>\d+) positions; at time=0, it is at position (?<initial>\d+).""")

private fun String.toDisc(): Disc {
    val groups = discRex.matchEntire(this)?.groups ?: error("Input error: $this")

    return Disc(
        id =  groups["id"]!!.value.toInt(),
        positions = groups["positions"]!!.value.toInt(),
        initialPosition = groups["initial"]!!.value.toInt(),
    )
}

private fun part2(input: List<String>): Int {
    val d = input.map { line -> line.toDisc() }
    val discs = (d + Disc(id = d.size + 1, positions = 11, initialPosition = 0)).sortedByDescending { it.positions }

    discs.joinToString("\n").println()

    return solve(discs)
}

private fun solve(discs: List<Disc>): Int {
    val bigDisc = discs.first()
    var time = bigDisc.firstAlignment

    val otherDiscs = discs.drop(1)

    while (true) {
        if (otherDiscs.all { disc -> disc.isSolution(time) }) {
            return time
        } else {
            time += bigDisc.positions
        }

        if (time > 10_000_000) {
            error("Time exceeded big value")
        }
    }
}
