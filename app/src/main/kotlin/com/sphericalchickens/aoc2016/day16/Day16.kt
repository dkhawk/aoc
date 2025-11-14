package com.sphericalchickens.aoc2016.day16

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 16 ---")

    val input = readInputLines("aoc2016/day16_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input, 272)
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part1(input, 35651584)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        1 100
        0 001
        11111 11111000000
        111100001010 1111000010100101011110000
    """.trimIndent().lines()

    testInput.forEach { line ->
        val (a, b) = line.split(" ")
        check("Part 1 Test Case $line", b, a.dragonExpand())
    }

    check("checksum", "100", "110010110100".dragonChecksum())

    check("all together", "01100", part1(listOf("10000"), 20))

}

private fun String.dragonChecksum(): String {
    return generateSequence(this) { str ->
        buildString {
            str.windowed(2, 2).forEach { append(if (it.first() == it.last()) '1' else '0') }
        }
    }.first { it.length.isOdd() }
}

private fun Int.isEven() = (this and 1) == 0
private fun Int.isOdd() = (this and 1) == 1

private fun String.dragonExpand(): String {
    val b = buildString {
        this@dragonExpand.reversed().forEach {
            append(
                when (it) {
                    '0' -> '1'
                    '1' -> '0'
                    else -> error("Invalid character: $it")
                }
            )
        }
    }

    return "${this}0$b"
}

private fun part1(input: List<String>, diskSize: Int): String {
    val s = generateSequence(input.first()) {
        it.dragonExpand()
    }.first { it.length >= diskSize }.take(diskSize)

    return s.dragonChecksum()
}
