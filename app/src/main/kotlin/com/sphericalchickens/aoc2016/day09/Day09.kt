package com.sphericalchickens.aoc2016.day09

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis
import kotlin.text.iterator

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 09 ---")

    val input = readInputLines("aoc2016/day09_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input.joinToString(separator = ""))
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
            val part2Result = part2(input.joinToString(separator = ""))
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        ADVENT
        A(1x5)BC
        (3x3)XYZ
        A(2x2)BCD(2x2)EFG
        (6x1)(1x3)A
        X(8x2)(3x3)ABCY
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 6, part1(testInput[0]))
    check("Part 1 Test Case 2", 7, part1(testInput[1]))
    check("Part 1 Test Case 3", 9, part1(testInput[2]))
    check("Part 1 Test Case 4", 11, part1(testInput[3]))
    check("Part 1 Test Case 5", 6, part1(testInput[4]))
    check("Part 1 Test Case 6", 18, part1(testInput[5]))
}

private fun runPart2Tests() {
    val testInput = """
        (3x3)XYZ
        X(8x2)(3x3)ABCY
        (27x12)(20x12)(13x14)(7x10)(1x12)A
        (25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "XYZXYZXYZ".length.toLong(), part2(testInput[0]))
    check("Part 2 Test Case 2", "XABCABCABCABCABCABCY".length.toLong(), part2(testInput[1]))
    check("Part 2 Test Case 3", 241920, part2(testInput[2]))
    check("Part 2 Test Case 4", 445, part2(testInput[3]))
}

private fun part1(input: String): Long {
    val iter = input.iterator()
    var count = 0L

    while (iter.hasNext()) {
        val next = iter.nextChar()
        if (next == '(') {
            val characterCount = buildString {
                while (iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next != 'x') {
                        append(next)
                    } else {
                        break
                    }
                }
            }.toInt()
            val multiplier  = buildString {
                while (iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next != ')') {
                        append(next)
                    } else {
                        break
                    }
                }
            }.toInt()
            count += characterCount * multiplier
            repeat(characterCount) { iter.next() }
        } else {
            count++
        }
    }

    return count
}

private fun part2(input: String): Long {
    val iter = input.iterator()
    var count = 0L

    while (iter.hasNext()) {
        val next = iter.nextChar()
        if (next == '(') {
            val characterCount = buildString {
                while (iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next != 'x') {
                        append(next)
                    } else {
                        break
                    }
                }
            }.toInt()
            val multiplier  = buildString {
                while (iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next != ')') {
                        append(next)
                    } else {
                        break
                    }
                }
            }.toInt()

            val substring = buildString {
                repeat(characterCount) {
                    append(iter.next())
                }
            }
            count += multiplier * part2(substring)

        } else {
            count++
        }
    }

    return count
}
