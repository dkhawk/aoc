package com.sphericalchickens.aoc2024.day01

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readLines
import kotlin.math.abs
import kotlin.time.measureTimedValue

val testInput = """
    3   4
    4   3
    2   5
    1   3
    3   9
    3   3""".trimIndent().trim().lines()

fun main() {
    val testInput = parseInput(testInput)

    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Or read a large test input from the `src/Day01_test.txt` file:
//    val testInput = readInput("inputs/01_test")
//    check(part1(testInput) == 1)

    // Read the input from the resources directory
    val input = parseInput(readInputLines("aoc2024/day01_input.txt"))
    val (p1, d1) = measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun parseInput(input: List<String>): List<Pair<Int, Int>> {
    return input.map { line ->
        line.split("""\s+""".toRegex()).map { it.trim().toInt() }
    }.map { l ->
        require(l.size == 2)
        l.first() to l.last()
    }
}

fun part1(input: List<Pair<Int, Int>>): Int {
    val (left, right) = input.unzip()
    return left.sorted().zip(right.sorted()).sumOf { abs(it.first - it.second) }
}

fun part2(input: List<Pair<Int, Int>>): Int {
    val (left, right) = input.unzip()
    val similarityMap = right.groupingBy { it }.eachCount().map { (k, v) -> k to k * v }.toMap().withDefault { 0 }
    return left.sumOf { similarityMap.getValue(it) }
}
