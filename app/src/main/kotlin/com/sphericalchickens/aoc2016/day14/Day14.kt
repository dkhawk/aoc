package com.sphericalchickens.aoc2016.day14

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis
import java.security.MessageDigest

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.fold("") { str, byte -> str + "%02x".format(byte) }
}

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 14 ---")

    val input = readInputLines("aoc2016/day14_input.txt")

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
        
    """.trimIndent().lines()
//    check("Part 1 Test Case 1", "expected", part1(testInput))
    val salt = "abc"

    createHash(salt, 18).also { it.println() }.firstTriplet()?.println()
    createHash(salt, 39).also { it.println() }.firstTriplet()?.println()
    createHash(salt, 816).hasQuintuplet('e').println()
}

private fun String.hasQuintuplet(ch: Char): Boolean {
    val needle = 'e'.toString().repeat(5)
    return this.contains(needle)
}

private fun createHash(salt: String, key: Int) : String {
    return "$salt$key".md5()
}

private fun showKey(salt: String, key: Int) {
    val md5 = createHash(salt, key)
    md5.println()
    md5.firstTriplet()?.println()
}

private fun String.firstTriplet(): Char? {
    for (i in this.indices.drop(2)) {
        if (this[i] == this[i + 1] && this[i] == this[i + 2]) {
            return this[i]
        }
    }

    return null
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun part1(input: List<String>): String {
    return ""
}

private fun part2(input: List<String>): String {
    return ""
}
