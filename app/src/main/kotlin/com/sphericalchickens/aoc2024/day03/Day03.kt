package com.sphericalchickens.aoc2024.day03

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readText

const val testInput = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"""
const val test2     = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""

val regexImproved = """(?<mult>mul\((?<first>[0-9]{1,3}),(?<second>[0-9]{1,3})\))|(?<do>do\(\))|(?<donot>don't\(\))""".toRegex()

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 161)
    check(part2(test2) == 48)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readText("inputs/03"))

    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun parseInput(input: String): String {
    return input
}

fun part1(input: String): Int {
    return regexImproved.findAll(input).fold(State()) { state, match ->
        match.product()?.let {
            state.copy(sum = state.sum + it)
        } ?: state
    }.sum
}

private fun MatchResult.product(): Int? {
    return groups["second"]?.value?.toInt()?.let { second ->
        groups["first"]?.value?.toInt()?.times(second)
    }
}

data class State(val sum: Int = 0, val enabled: Boolean = true)

fun part2(input: String): Int {
    return regexImproved.findAll(input).fold(State()) { state, match ->
        when {
            match.groups["mult"] != null && state.enabled -> state.copy(sum = state.sum + match.product()!!) // will fail for invalid mul operation
            match.groups["do"] != null -> state.copy(enabled = true)
            match.groups["donot"] != null -> state.copy(enabled = false)
            else -> state
        }
    }.sum
}

//// Improved multiply function that uses the better regex to get the factors
//private fun multiply2(g: MatchResult): Int {
//    return g.groupValues.drop(1).fold(1) { product, factor ->
//        product * factor.toInt()
//    }
//}
//
//// First version that was "good enough"
//private fun multiply(g: MatchResult): Int {
//    val (a, b) = g.value.split(",")
//    val c = a.substringAfter("(").toInt()
//    val d = b.substringBefore(")").toInt()
//    return c * d
//}
