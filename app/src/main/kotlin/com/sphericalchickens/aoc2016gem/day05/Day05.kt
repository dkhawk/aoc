package com.sphericalchickens.aoc2016gem.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

/**
 * Main entry point for the solution.
 * Sets up the environment, reads the input, and controls the execution
 * of tests and solutions for both parts of the puzzle.
 */
fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 05 (Sequential) ---")

    val input = readInputLines("aoc2016/day05_input.txt").first()

    // Encapsulate all puzzle logic within a dedicated solution class.
    val solution = Day05Solution(input)

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        solution.runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = solution.part1()
            println("   Part 1: $part1Result") // Expected: f97c354d
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        solution.runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = solution.part2()
            println("   Part 2: $part2Result") // Expected: 863dde27
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

/**
 * Encapsulates the logic for Day 5.
 *
 * The core of this class is the `validHashes` sequence, which lazily
 * generates the required MD5 hashes. This sequence is then consumed
 * by the `part1` and `part2` functions to produce their respective solutions.
 *
 * @param doorId The puzzle input string.
 */
private class Day05Solution(private val doorId: String) {

    /**
     * A lazy sequence that generates MD5 hashes for 'doorId' + index,
     * filtering only for those that start with five zeros.
     *
     * Using a Sequence is highly efficient because:
     * 1. Hashes are computed one at a time, only when requested.
     * 2. It avoids storing a large list of hashes in memory.
     * 3. The `MessageDigest` instance is created only once.
     */
    private val validHashes: Sequence<String> = sequence {
        // MD5 instance is created once and reused for performance.
        val md5 = MessageDigest.getInstance("MD5")
        var index = 0
        while (true) {
            val inputBytes = (doorId + index).toByteArray()
            val hashBytes = md5.digest(inputBytes)

            // Efficiently check for 5 leading zeros (20 bits).
            // This is true if the first two bytes are 0 and the high nibble
            // of the third byte is 0. This check avoids the expensive conversion
            // to a hex string for invalid hashes.
            if (hashBytes[0].toInt() == 0 && hashBytes[1].toInt() == 0 && (hashBytes[2].toInt() and 0xF0) == 0) {
                // Yield the full hex string only when a valid hash is found.
                yield(hashBytes.joinToString("") { "%02x".format(it) })
            }
            index++
        }
    }

    /**
     * Solves Part 1 of the puzzle.
     * The password is the 6th character (index 5) of the first 8 valid hashes.
     */
    fun part1(): String {
        return validHashes
            .map { hash -> hash[5] } // Transform each valid hash to its 6th character.
            .take(8) // We only need the first 8 such characters.
            .joinToString("") // Concatenate them into the final password.
    }

    /**
     * Solves Part 2 of the puzzle.
     * The 6th character of a hash is the position (0-7), and the 7th is the value.
     * The password is formed by finding the first valid character for each position.
     */
    fun part2(): String {
        return validHashes
            .map { hash -> hash[5] to hash[6] } // Pair the position char with the value char.
            .filter { (pos, _) -> pos in '0'..'7' } // Keep only hashes with a valid position.
            .distinctBy { (pos, _) -> pos } // Crucially, keep only the FIRST hash found for each position.
            .take(8) // We are done once we have found 8 unique positions.
            .sortedBy { (pos, _) -> pos } // Sort by position to assemble the password correctly.
            .map { (_, value) -> value } // We only need the value character now.
            .joinToString("") // Join the characters to form the final password.
    }

    /**
     * Runs test cases for Part 1.
     */
    fun runPart1Tests() {
        check(
            "Part 1 Test Case 1",
            "18f47a30",
            Day05Solution("abc").part1()
        )
    }

    /**
     * Runs test cases for Part 2.
     */
    fun runPart2Tests() {
        check(
            "Part 2 Test Case 1",
            "05ace8e3",
            Day05Solution("abc").part2()
        )
    }
}
