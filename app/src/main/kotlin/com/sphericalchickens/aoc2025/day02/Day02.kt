package com.sphericalchickens.aoc2025.day02

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputText
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 2 ---")

    val input = readInputText("aoc2025/day02_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimedValue {
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
        val timeInMillis = measureTimedValue {
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
        1698522-1698528,446443-446449,38593856-38593862,565653-565659,
        824824821-824824827,2121212118-2121212124
    """.trimIndent().replace("\n", "")

    check("11-22", listOf(11L, 22L), findInvalidIds("11-22"))
    check("38593856-38593862", listOf(38593859L), findInvalidIds("38593856-38593862"))
    check("Part 1 Test Case 1", 1227775554L, part1(testInput))
}

private fun findInvalidIds(range: String): List<Long> {
    val (first, last) = range.split("-").map(String::trim).map(String::toLong)

    return (first..last).mapNotNull { value ->
        val s = value.toString()
        val h = s.length / 2
        if (s.length.isEven() && s.take(h) == s.substring(h, s.length)) s.toLong() else null
    }
}

private fun Int.isEven(): Boolean = (this % 2) == 0

private fun runPart2Tests() {
    val testInput = """
        11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
        1698522-1698528,446443-446449,38593856-38593862,565653-565659,
        824824821-824824827,2121212118-2121212124
    """.trimIndent().replace("\n", "")

    check("11-22", listOf(11L, 22L), findInvalidIds2("11-22"))
    check("38593856-38593862", listOf(38593859L), findInvalidIds2("38593856-38593862"))
    check("2121212118-2121212124", listOf(2121212121L), findInvalidIds2("2121212118-2121212124"))
    check("Part 2 Test Case 1", 4174379265L, part2(testInput))
}

private fun findInvalidIds2(range: String): List<Long> {
    val (first, last) = range.split("-").map(String::trim).map(String::toLong)

    return (first..last).mapNotNull { value ->
        val s = value.toString()
        val h = s.length / 2

        var isInvalid = false

        for (ws in 1..h) {
            //            if (s.length % ws == 0) {
            //                if (s.chunked(ws).toSet().size == 1) {
            //                    isInvalid = true
            //                    break
            //                }
            //            }

            // This is slightly more performant
            if (s.length % ws == 0) {
                val chunks = s.chunked(ws)
                if (chunks.all { it == chunks.first() }) {
                    isInvalid = true
                    break
                }
            }


            //            val ss = s.take(ws)
            //            if (s.windowed(ws, ws, true).all { it == ss }) {
            //                isInvalid = true
            //                break
            //            }
        }

        if (isInvalid) value else null
    }
}

private fun part1(input: String): Long {
    return input.split(",").flatMap { findInvalidIds(it) }.sum()
}

private fun part2(input: String): Long {
    return input.split(",").flatMap { findInvalidIds2(it) }.sum()
}
