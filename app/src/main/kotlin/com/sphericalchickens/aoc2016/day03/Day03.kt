package com.sphericalchickens.aoc2016.day03

import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.text.trim

fun main() {
    println("--- Advent of Code 2016, Day 3 ---")

    val input = readInputLines("aoc2016/day03_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")
}

fun part1(input: List<String>): Int {
    return input.filterNot { it.isBlank() }.count {
        it.isTriangle()
    }
}

fun part2(input: List<String>): Int {
    val values = input.map { line -> line.trim().split(Regex("""\s+""")).map { it.toInt() }}.flatten()

    val pivoted = buildList {
        for (i in values.indices) {
            if (i % 3 == 0) add(values[i])
        }
        for (i in values.indices) {
            if (i % 3 == 1) add(values[i])
        }
        for (i in values.indices) {
            if (i % 3 == 2) add(values[i])
        }
    }

    return pivoted.windowed(3, 3).count { isTriangle(it) }
}

private fun String.isTriangle() : Boolean {
    val sides = trim().split(Regex("""\s+""")).map { it.toInt() }

    return isTriangle(sides)
}

private fun isTriangle(sides: List<Int>): Boolean = (sides[0] + sides[1] > sides[2]) &&
        (sides[1] + sides[2] > sides[0]) &&
        (sides[0] + sides[2] > sides[1])

private fun runTests() {
    // Add tests here
}
