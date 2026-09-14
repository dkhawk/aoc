package com.sphericalchickens.aoc2024.day07

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.println
import kotlinx.coroutines.*
import kotlin.time.measureTime

val testInput = """
    190: 10 19
    3267: 81 40 27
    83: 17 5
    156: 15 6
    7290: 6 8 6 15
    161011: 16 10 13
    192: 17 8 14
    21037: 9 7 18 13
    292: 11 6 16 20""".trimIndent()

fun main() = runBlocking {
    val input = readInputLines("aoc2024/day07_input.txt")

    check(part1(testInput.lines()) == 3749L)
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")

    check(part2(testInput.lines()) == 11387L)
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun part1(input: List<String>): Long {
    val equations = input.map { it.split(":? ".toRegex()).map(String::toLong) }

    return equations.sumOf { equation ->
        checkEquation(equation)
    }
}

fun checkEquation(equation: List<Long>): Long {
    val expected = equation.first()
    val factors = equation.drop(1).reversed()

    val answer = calculate(expected, factors.first(), factors.drop(1))
    if (answer.any { it == expected}) {
        return expected
    }
    return 0
}

fun calculate(expected: Long, acc: Long, rest: List<Long>): List<Long> {
    if (rest.isEmpty()) {
        return listOf(acc)
    }

    // Addition
    return calculate(expected, rest.first(), rest.drop(1)).flatMap { it ->
        buildList {
            (acc + it).also { sum -> if (sum <= expected) add(sum) }
            (acc * it).also { product -> if (product <= expected) add(product) }
        }
    }
}

fun part2(input: List<String>): Long {
    val equations = input.map { it.split(":? ".toRegex()).map(String::toLong) }

    return equations.sumOf { equation ->
        checkEquation2(equation)
    }
}

fun checkEquation2(equation: List<Long>): Long {
    val expected = equation.first()
    val factors = equation.drop(1).reversed()

    val answer = calculate2(expected, factors.first(), factors.drop(1))
    if (answer.any { it == expected}) {
        return expected
    }
    return 0
}

fun calculate2(expected: Long, acc: Long, rest: List<Long>): List<Long> {
    if (rest.isEmpty()) {
        return listOf(acc)
    }

    // Addition
    return calculate2(expected, rest.first(), rest.drop(1)).flatMap { it ->
        buildList {
            (acc + it).also { sum -> if (sum <= expected) add(sum) }
            (acc * it).also { product -> if (product <= expected) add(product) }
            (it.toString() + (acc.toString())).toLong().also { product -> if (product <= expected) add(product) }
        }
    }
}
