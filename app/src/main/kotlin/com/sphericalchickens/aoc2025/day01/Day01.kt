package com.sphericalchickens.aoc2025.day01

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

    println("--- Advent of Code 2025, Day 1 ---")

    val input = readInputLines("aoc2025/day01_input.txt")

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
        runPart2cTests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            part2c(input)
        }
        println("   Part 2: $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}")
    }
}

private fun runPart1Tests() {
    val testInput = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 3, part1(testInput))
}

private val inputRegex = Regex("""(-?\d+) to (-?\d+) -> (\d+)""")

private fun runPart2cTests() {
    val input = """
        50 to -18 -> 1
        -18 to -48 -> 0
        -48 to 0 -> 1
        0 to -5 -> 0
        -5 to 55 -> 1
        55 to 0 -> 1
        0 to -1 -> 0
        -1 to -100 -> 1
        -100 to -86 -> 0
        -86 to -168 -> 1
    """.trimIndent().lines()

    input.forEach { line ->
        val (start, end, count) = inputRegex.matchEntire(line)!!.groupValues.drop(1).map(String::toInt)
        check("$start, $end, $count", count, (start..end).count100Crossings())
    }

    val testInput = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()
    check("Part 2 Test Case 1", 6, part2c(testInput))

}

private fun part1(input: List<String>): Int {
    return input
        .map(String::toSignedDistance)
        .runningFold(50) { acc, distance ->
            acc + distance
        }.count { it.mod(100) == 0 }
}

private fun String.toSignedDistance() = drop(1).toInt().let { if (this[0] == 'R') it else -it }

private fun part2c(input: List<String>): Int {
    return input
        .map(String::toSignedDistance)
        .runningFold(0..50) { s, distance ->
            (s.last..(s.last + distance))
        }.sumOf { it.count100Crossings() }
}

private fun IntRange.count100Crossings(): Int {
    return if (this.first < this.last) {
        var count = 0
        var c = this.first.next100()

        if (c == this.first)
            c += 100

        while (c <= this.last) {
            c += 100
            count += 1
        }
        count
    } else {
        var count = 0
        var c = this.first.next100() - 100

        if (c == this.first)
            c += 100

        while (c >= this.last) {
            c -= 100
            count += 1
        }
        count
    }
}

private fun Int.next100(): Int {
    if (this.mod(100) == 0) {
        return this
    }

    return if (this < 0) {
        (this / 100) * 100
    } else {
        ((this / 100) + 1) * 100
    }
}
