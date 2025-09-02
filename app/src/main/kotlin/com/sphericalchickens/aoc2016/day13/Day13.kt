package com.sphericalchickens.aoc2016.day13

import com.sphericalchickens.utils.readInputLines

fun main() {
    println("--- Advent of Code 2016, Day 13 ---")

    val input = readInputLines("aoc2016/day13_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")
}

fun part1(input: List<String>): Int {
    return input.size
}

fun part2(input: List<String>): Int {
    return input.size
}

private fun runTests() {
    // Add tests here
}
