package com.sphericalchickens.aoc2025.day11

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 11 ---")

    val input = readInputLines("aoc2025/day11_input.txt")

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
        aaa: you hhh
        you: bbb ccc
        bbb: ddd eee
        ccc: ddd eee fff
        ddd: ggg
        eee: out
        fff: out
        ggg: out
        hhh: ccc fff iii
        iii: out
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 5, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        svr: aaa bbb
        aaa: fft
        fft: ccc
        bbb: tty
        tty: ccc
        ccc: ddd eee
        ddd: hub
        hub: fff
        eee: dac
        dac: fff
        fff: ggg hhh
        ggg: out
        hhh: out
    """.trimIndent().lines()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private val re = Regex(""":?\s+""")

private fun part1(input: List<String>): Long {
    val pathsToOut = mutableMapOf<String, Long>()

    val network = input.associate { line ->
        val nodes = line.split(re)
        nodes.first() to nodes.drop(1)
    }

    return countPaths(network, "you", "out", pathsToOut)
}

private fun countPaths(
    network: Map<String, List<String>>,
    location: String,
    goal: String,
    pathsToOut: MutableMap<String, Long>
): Long {
    return pathsToOut.getOrPut(location) {
        if (location == goal) {
            1
        } else {
            network.getValue(location).mapNotNull { countPaths(network, it, goal, pathsToOut) }.sum()
        }
    }
}

private fun part2(input: List<String>): Int {
    return -1
}
