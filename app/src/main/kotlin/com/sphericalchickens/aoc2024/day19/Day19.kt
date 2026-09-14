package com.sphericalchickens.aoc2024.day19

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*

import kotlin.time.measureTime

val testInput = """
    r, wr, b, g, bwu, rb, gb, br

    brwrr
    bggr
    gbbr
    rrbgbr
    ubwu
    bwurrg
    brgr
    bbrgwb
""".trimIndent().lines()

fun main() = runBlocking {
    check(part1(testInput) == 6)
    check(part2(testInput) == 16L)

    val input = readLines("inputs/19")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun part1(input: List<String>): Int {
    val towels = input.first().split(",").map { it.trim() }
    val patterns = input.drop(2)

    val regex = towels.joinToString("|", prefix = "(", postfix = ")+").toRegex()

    val jobs = withContext(Dispatchers.Default) {
         patterns.map { pattern ->
            GlobalScope.async {
                checkPattern2(pattern, regex) //.also { "$pattern -> $it".println() }
            }
        }

    }

    return jobs.awaitAll().count { it }
}

fun checkPattern2(pattern: String, regex: Regex): Boolean {
//    val regex = """(r|wr|b|g|bwu|rb|gb|br)+""".toRegex()
    return regex.matchEntire(pattern) != null
}

// First, naive impl.  Bad!
fun checkPattern(pattern: String, towels: List<String>): Boolean {
    val queue = ArrayDeque<String>()

    queue.addLast("")

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val toMatch = pattern.substring(current.length)

        val possibleMatches = towels.filter { towel ->
            toMatch.startsWith(towel)
        }

        possibleMatches.forEach { towel ->
            val updated = current + towel

            if (updated == pattern) {
                "pattern: $pattern can be made".println()
                return true
            }
            queue.add(updated)
        }
    }

    "pattern: $pattern cannot be made".println()
    return false
}

val previousMatches = mutableMapOf<String, Long>()

private fun part2(input: List<String>): Long {
    val towels = input.first().split(",").map { it.trim() }
    val patterns = input.drop(2)

    previousMatches.clear()

    return patterns.sumOf { pattern ->
        countMatches(towels, pattern)
    }
}

fun countMatches(towels: List<String>, pattern: String): Long {
    if (pattern.isEmpty()) {
        return 1L
    }

    // Have we already seen this pattern?
    if (previousMatches.containsKey(pattern)) {
        return previousMatches[pattern]!!
    }

    val  matchingTowels = towels.filter { towel -> pattern.startsWith(towel) }

    if (matchingTowels.isEmpty()) {
        return 0
    }

    val numberOfMatches = matchingTowels.sumOf { towel ->
        val rest = pattern.substring(towel.length)
        countMatches(towels, rest)
    }

    previousMatches[pattern] = numberOfMatches

    return numberOfMatches
}
