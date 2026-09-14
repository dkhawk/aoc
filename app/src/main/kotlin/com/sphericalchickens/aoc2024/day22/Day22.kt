package com.sphericalchickens.aoc2024.day22

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*


val testInput = """
    1
    10
    100
    2024
""".trimIndent().lines()

val testInput2 = """
    1
    2
    3
    2024
""".trimIndent().lines()

fun main() {
    check(part1(testInput) == 37327623L)
    check(part2(testInput2) == 23)

    val input = readLines("inputs/22")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

private fun part1(input: List<String>): Long {
    return runBlocking {
        withContext(Dispatchers.Default) {
            input.map { line -> line.toLong() }.map {
                async { getSecret(it, 2000) }
            }
        }.awaitAll().sum()
    }
}

fun getSecret(initial: Long, reps: Int): Long {
    var secret = initial
    repeat(reps) {
        secret = secret.next() // .also { it.println() }
    }
    return secret
}

fun getSecretSequenceOnes(seed: Long): Sequence<Int> {
    var secret = seed
    return sequence {
        while (true) {
            yield((secret % 10).toInt())
            secret = secret.next()
        }
    }
}

private fun Long.prune(): Long {
    return this % 16777216
}

private fun Long.next(): Long {
    var s = this
    s = (s * 64).mix(s).prune()
    s = (s / 32).mix(s).prune()
    return (s * 2048).mix(s).prune()
}

private fun Long.mix(other: Long): Long {
    return this xor other
}

private fun part2(input: List<String>): Int {
    val codes = runBlocking {
        withContext(Dispatchers.Default) {
            input.map { line -> line.toLong() }.map {
                async {
                    val ones = getSecretSequenceOnes(it).take(2000).toList()
                    val deltas = ones.windowed(2, 1) { it[1] - it[0] }
                    val sequences = deltas.windowed(4, 1) {
                        it.take(4)
                    }

                    val firstOccurrences = mutableMapOf<String, Int>()

                    sequences.forEachIndexed { index, seq ->
                        val value = ones[(index + 4)]
                        val key = seq.joinToString(",")
                        if (!firstOccurrences.containsKey(key)) {
                            firstOccurrences[key] = value
                        }
                    }

                    firstOccurrences
                }
            }
        }.awaitAll().toList()
    }

    val allKeys = codes.map { it.keys }.fold(mutableSetOf<String>()) { set, keys ->
        set.addAll(keys)
        set
    }

    val x = allKeys.map { key ->
        key to codes.mapNotNull { it[key] }.sum()
    }

    return x.maxBy { it.second }.second
}
