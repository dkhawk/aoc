package com.sphericalchickens.aoc2016gem.day03

import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines

fun main() {
    println("--- Advent of Code 2016, Day 3 (Gemini) ---")

    val input = readInputLines("aoc2016/day03_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")
}

/**
 * Counts the number of valid triangles, with each line of input representing one triangle.
 */
fun part1(input: List<String>): Int {
    return input.count { it.isTriangle() }
}

/**
 * Counts the number of valid triangles, with triangles read vertically down columns.
 */
fun part2(input: List<String>): Int {
    val numbers = input
        .filterNot { it.isBlank() }
        .map { line ->
            line.trim().split(Regex("""\s+""")).map { it.toInt() }
        }

    // We now have a list of lists, where each inner list is a row of numbers.
    // To process the triangles column-by-column, we can "transpose" the data,
    // turning columns into rows.
    val transposed = numbers.transpose()

    // After transposing, each inner list is a column from the original input.
    // We can now chunk these columns into groups of three to form the triangles.
    return transposed.flatten().chunked(3).count { isTriangle(it) }
}

/**
 * An extension function to check if a string of numbers can form a valid triangle.
 */
private fun String.isTriangle(): Boolean {
    val sides = this.trim().split(Regex("""\s+""")).mapNotNull { it.toIntOrNull() }
    return isTriangle(sides)
}

/**
 * Checks if a list of three integers can form a valid triangle.
 * A triangle is valid if the sum of its two shorter sides is greater than the longest side.
 */
private fun isTriangle(sides: List<Int>): Boolean {
    if (sides.size != 3) return false
    val sortedSides = sides.sorted()
    return sortedSides[0] + sortedSides[1] > sortedSides[2]
}

/**
 * Transposes a list of lists, turning rows into columns.
 * For example:
 * [[1, 2, 3], [4, 5, 6]] -> [[1, 4], [2, 5], [3, 6]]
 */
private fun <T> List<List<T>>.transpose(): List<List<T>> {
    if (isEmpty() || first().isEmpty()) return emptyList()

    return List(first().size) { colIndex ->
        List(size) { rowIndex ->
            this[rowIndex][colIndex]
        }
    }
}
