package com.sphericalchickens.aoc2025gem.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputText
import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true

    println("--- Advent of Code 2025, Day 5 (Refactored) ---")

    // We assume the input file exists at this path relative to the project root
    val input = readInputText("aoc2025/day05_input.txt")

    if (runPart1Tests) runPart1Tests()
    if (runPart1Solution) {
        val (result, duration) = measureTimedValue { Day05(input).solvePart1() }
        println("🎁 Part 1: $result (in ${duration})")
    }

    if (runPart2Tests) runPart2Tests()
    if (runPart2Solution) {
        val (result, duration) = measureTimedValue { Day05(input).solvePart2() }
        println("🎀 Part 2: $result (in ${duration})")
    }
}

class Day05(input: String) {
    // We separate the two sections of the input file: Range definitions and specific numbers.
    // Using trim() ensures no leading/trailing whitespace issues.
    private val sections = input.trim().split("\n\n")

    // Parsing the Recipes (Ranges)
    // We Map each line to a LongRange (e.g., "10-20" -> 10L..20L).
    private val recipes: List<LongRange> = sections.first().lines()
        .filter { it.isNotBlank() }
        .map { line ->
            val (start, end) = line.split("-").map { it.toLong() }
            start..end
        }

    // Parsing the Ingredients (Numbers)
    private val ingredients: List<Long> = sections.getOrElse(1) { "" }.lines()
        .filter { it.isNotBlank() }
        .map { it.trim().toLong() }

    // ... logic continues below
    /**
     * Merges overlapping ranges.
     * Complexity: O(N log N) due to sorting, where N is the number of recipes.
     */
    private fun List<LongRange>.mergeOverlaps(): List<LongRange> {
        if (isEmpty()) return emptyList()

        val sorted = this.sortedBy { it.first }

        return buildList {
            var current = sorted.first()

            for (next in sorted.drop(1)) {
                // Check if the next range starts strictly after the current range ends.
                if (next.first > current.last) {
                    add(current)
                    current = next
                } else {
                    // Overlap detected: Extend the current range if needed.
                    val newEnd = max(current.last, next.last)
                    current = current.first..newEnd
                }
            }
            add(current)
        }
    }

    fun solvePart1(): Int {
        // Simple check: does the ingredient exist in ANY recipe range?
        return ingredients.count { ingredient ->
            recipes.any { recipe -> ingredient in recipe }
        }
    }

    fun solvePart2(): Long {
        // Merge ranges and sum their lengths.
        val merged = recipes.mergeOverlaps()
        return merged.sumOf { (it.last - it.first) + 1 }
    }
}

// --- Test Runners ---

private fun runPart1Tests() {
    val testInput = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent()

    check("Part 1 Test Case 1", 3, Day05(testInput).solvePart1())
}

private fun runPart2Tests() {
    val testInput = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent()
    check("Part 2 Test Case 1", 14L, Day05(testInput).solvePart2())
}
