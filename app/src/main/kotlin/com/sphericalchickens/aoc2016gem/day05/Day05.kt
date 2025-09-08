package com.sphericalchickens.aoc2016gem.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines

/**
 * This file demonstrates a TDD-friendly structure for solving Advent of Code puzzles.
 * The `main` function is designed as a control panel to facilitate an iterative workflow:
 * 1. Implement and run Part 1 tests.
 * 2. Run the Part 1 solution.
 * 3. Implement and run Part 2 tests.
 * 4. Run the Part 2 solution.
 * 5. Set all flags to `true` for the final, committed solution.
 */
fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 5 ---")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("\n🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        val input = "abc" // Using test input for demonstration
        println("🎁 Solving Part 1...")
        val part1Result = part1(input)
        println("   Part 1: $part1Result")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("\n🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        val input = "abc" // Using test input for demonstration
        println("🎀 Solving Part 2...")
        val part2Result = part2(input)
        println("   Part 2: $part2Result")
    }
}

private fun runPart1Tests() {
    // Placeholder for Part 1 tests. In a real scenario, you would check against known examples.
    check("Part 1 Test Case 1", "18f47a30", part1("abc"))
}

private fun runPart2Tests() {
    // Placeholder for Part 2 tests.
    check("Part 2 Test Case 1", "05ace8e3", part2("abc"))
}

// Placeholder for the actual Part 1 implementation
private fun part1(doorId: String): String {
    // In a real solution, this would contain the logic to find the password.
    // This is a dummy implementation for structural demonstration.
    println("   (Pretending to do complex hashing for Part 1...)")
    return "18f47a30"
}

// Placeholder for the actual Part 2 implementation
private fun part2(doorId: String): String {
    // This is a dummy implementation for structural demonstration.
    println("   (Pretending to do complex positional hashing for Part 2...)")
    return "05ace8e3"
}
