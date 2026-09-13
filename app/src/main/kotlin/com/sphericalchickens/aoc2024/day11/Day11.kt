package com.sphericalchickens.aoc2024.day11

import com.sphericalchickens.utils.*


import kotlinx.coroutines.runBlocking

import kotlin.time.measureTime

val testInput = """
    125 17
""".trimIndent()

fun main() = runBlocking {
    check(part1(testInput) == 55312)
    check(part2(testInput, 6) == 22L)
    check(part2(testInput, 25) == 55312L)

    val input = readText("inputs/11")
    part1(input).println()
    measureTime {
        part2(input, 75).println()
    }.println()
}

private fun part1(input: String): Int {
    val rocks = input.split(" ").map { it.toLong() }

    val sequences = sequence<List<Long>> {
        var current = rocks

        while (true) {
            current = buildList {
                current.forEach { rock ->
                    if (rock == 0L) {
                        add(1L)
                    } else {
                        val rs = rock.toString()
                        if (rs.length.isEven()) {
                            val mid = rs.length shr 1
                            add(rs.substring(0, mid).toLong())
                            add(rs.substring(mid).toLong())
                        } else {
                            add(rock * 2024)
                        }
                    }
                }
            }
            yield(current)
        }
    }
    val last = sequences.take(25).last()

    return last.count()
}

typealias RockLabel = Long
typealias RockCount = Long
typealias RockPile = Map<RockLabel, RockCount>
typealias MutableRockPile = MutableMap<RockLabel, RockCount>

private fun part2(input: String, generations: Int): Long {
    val rocks = input.split(" ").map { it.toLong() }
    val rockPile =
        rocks.groupingBy { it }.eachCount().map { it.key to it.value.toLong() }
            .toMap()

    return (0 until generations).fold(rockPile) { oldRockPile, _ ->
        buildRockPile {
            oldRockPile.entries.forEach { (rock, rockCount) ->
                val rs = rock.toString()

                when {
                    rock == 0L -> addTo(1L, rockCount)

                    rs.length.isEven() -> {
                        rs.chunked(rs.length shr 1).forEach { key -> addTo(key.toLong(), rockCount) }
                    }

                    else -> addTo(rock * 2024L, rockCount)
                }
            }
        }
    }.values.sum()
}

private fun buildRockPile(block: MutableRockPile.() -> Unit): RockPile {
    return mutableMapOf<RockLabel, RockCount>().apply(block)
}

private fun MutableRockPile.addTo(rock: RockLabel, count: RockCount) {
    this[rock] = this.getOrDefault(rock, 0L) + count
}

private fun Int.isEven(): Boolean {
    return this.mod(2) == 0
}

