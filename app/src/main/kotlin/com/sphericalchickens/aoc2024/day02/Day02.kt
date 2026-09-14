package com.sphericalchickens.aoc2024.day02

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readLines
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.time.measureTime

val testInput = """
    7 6 4 2 1
    1 2 7 8 9
    9 7 6 2 1
    1 3 2 4 5
    8 6 4 4 1
    1 3 6 7 9""".trimIndent().trim().lines()

fun main() {
    val testInput = parseInput(testInput)


    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

//    val testInput = readInput("inputs/02_test")
//    check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readLines("inputs/02"))
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { line ->
        line.split("""\s+""".toRegex()).map { it.trim().toInt() }
    }
}

fun part1(input: List<List<Int>>): Int {
    return input.count(::isReportSafe)
}

fun part2(input: List<List<Int>>): Int {
//    return input.count(::isAnyReportSafeDequeues)
    return input.count(::isAnyReportSafeSequences)
}

fun isReportSafe(report: Collection<Int>) = isReportSafe(report.asSequence())

fun isReportSafe(report: Sequence<Int>): Boolean {
    val deltas = report.zipWithNext { a, b -> b - a }

    var direction: Int? = null

    return deltas.all { delta ->
        val sign = delta.sign
        val value = delta.absoluteValue

        val directionOkay = if (direction == null) {
            direction = sign
            // Short circuit a bad initial direction
            if (direction == 0) return@all false
            true
        } else {
            direction == sign
        }

        directionOkay && (value in 1..3)
    }
}

fun isAnyReportSafeSequences(report: List<Int>): Boolean {
    if (isReportSafe(report)) return true

    // Creates a sequence of sequences of Ints where each nested sequence drops a single value from the report
    // If _any_ succeed then we can stop checking
    return sequence {
        // Iterate over the indices of the report
        report.indices.map { indexUnderTest ->
            // Emit another sequence with the indexUnderTest item skipped
            yield(
                sequence {
                    // Skip the index under test value
                    report.indices.forEach { index ->
                        if (index != indexUnderTest) this.yield(report[index])
                    }
                }
            )
        }
    }.any(::isReportSafe)
}

fun isAnyReportSafeDequeues(report: List<Int>): Boolean {
    val pre = ArrayDeque<Int>()
    val post = ArrayDeque(report)

    if (isSafe(pre, post)) {
        return true
    }

    var itemUnderTest: Int

    while (post.isNotEmpty()) {
        itemUnderTest = post.removeFirst()
        if (isSafe(pre, post)) {
            return true
        }
        pre.add(itemUnderTest)
    }

    return false
}

private fun isSafe(
    pre: ArrayDeque<Int>,
    post: ArrayDeque<Int>
) = isReportSafe(
    sequence {
        pre.forEach { yield(it) }
        post.forEach { yield(it) }
    }
)
