package com.sphericalchickens.aoc2016gem.day16

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 16 (Refactored) ---")

    val input = readInputLines("aoc2016/day16_input.txt").first()

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = solve(input, 272)
            println("   Part 1: $part1Result")
        }
        println("   Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            // Using the same 'solve' function for Part 2
            val part2Result = solve(input, 35651584)
            println("   Part 2: $part2Result")
        }
        println("   Part 2 runtime: $timeInMillis ms.")
    }
}

private fun solve(initialInput: String, diskSize: Int): String {
    val data = generateSequence(initialInput) {
        it.dragonExpand()
    }.first { it.length >= diskSize }.take(diskSize)

    return data.dragonChecksum()
}

private fun String.dragonExpand(): String {
    // b = A.reversed().inverted()
    val b = buildString(length) {
        this@dragonExpand.reversed().forEach {
            append(if (it == '0') '1' else '0')
        }
    }
    // return A + 0 + B
    return "${this}0$b"
}

private fun String.dragonChecksum(): String {
    return generateSequence(this) { previousChecksum ->
        buildString(previousChecksum.length / 2) {
            previousChecksum.windowed(2, 2).forEach { pair ->
                append(if (pair[0] == pair[1]) '1' else '0')
            }
        }
    }.first { it.length % 2 != 0 }
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

    check("all together", "01100", solve("10000", 20))
}