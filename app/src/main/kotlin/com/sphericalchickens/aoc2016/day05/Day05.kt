package com.sphericalchickens.aoc2016.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis
import java.security.MessageDigest

private fun String.toMd5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 05 ---")

    val input = readInputLines("aoc2016/day05_input.txt")

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

private val digest = MessageDigest.getInstance("MD5")

private fun nextInterestingHash(input: String, start: Int = 0): Int {
    val numbers = generateSequence(start) { it + 1 }

    val numberOfLeadingZeros = 5

    val zeroBytes = numberOfLeadingZeros / 2
    val remainder = numberOfLeadingZeros % 2

    return numbers.map { index ->
        index to digest.digest((input + index.toString()).toByteArray())
    }.first {(_, hashBytes) ->
        hashBytes.take(zeroBytes).all { it == 0.toByte() } && hashBytes[zeroBytes].toUByte() < 0x10.toUByte()
    }.first
}

private fun runPart1Tests() {
    "abc3231928".toMd5().println()
    "abc3231929".toMd5().println()

    check("Part 1 Test Case 1",
        3231929,
        nextInterestingHash("abc", start = 3231929 - 10)
    )

    check("Part 1 Test Case 2",
        5017308,
        nextInterestingHash("abc", start = 3443786 - 1)
    )

    check(
        "Part 1 Test Case 3",
        "18f47a30",
        part1(listOf("abc"))
    )
}

private fun runPart2Tests() {
    check(
        "Part 2 Test Case 1",
        "05ace8e3",
        part2(listOf("abc"))
    )
}

private fun part1(input: List<String>): String {
    val doorId = input.first()
    var index = 0

    return buildList {
        for (ii in 0..7) {
            index = nextInterestingHash(doorId, start = index)
            val hex = (doorId + index.toString()).toMd5()
            val c = hex[5]
            add(c)
            index += 1
        }
    }.joinToString("")
}

private fun part2(input: List<String>): String {
    val doorId = input.first()
    var index = 0

    val password = "________".toMutableList()

    while (true) {
        index = nextInterestingHash(doorId, start = index)
        val hex = (doorId + index.toString()).toMd5()
        val candidate= hex[5].digitToIntOrNull(16) ?: continue

        if (candidate in 0 .. 7) {
            if (password[candidate] == '_') {
                password[candidate] = hex[6]
                val result = password.joinToString("").also { it.println() }
                if (!password.contains('_')) return result
            }
        }

        index += 1
    }
}
