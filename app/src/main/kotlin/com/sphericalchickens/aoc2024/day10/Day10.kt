package com.sphericalchickens.aoc2024.day10

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*

import kotlin.time.measureTime

val testInput = """
    89010123
    78121874
    87430965
    96549874
    45678903
    32019012
    01329801
    10456732
""".trimIndent()

val testInput2 = """
    .....0.
    ..4321.
    ..5..2.
    ..6543.
    ..7..4.
    ..8765.
    ..9....
""".trimIndent()

val testInput3 = """
    ..90..9
    ...1.98
    ...2..7
    6543456
    765.987
    876....
    987....
""".trimIndent()

val testInput4 = """
    012345
    123456
    234567
    345678
    4.6789
    56789.
""".trimIndent()

fun main() = runBlocking {
    check(part1(testInput) == 36)
    check(part2(testInput2) == 3)
    check(part2(testInput3) == 13)
    check(part2(testInput4) == 227)
    check(part2(testInput) == 81)

    val input = readText("inputs/10")
    part1(input).println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: String): Int {
    val grid = buildGrid(input.lines()).withDefault { '.' }

    val trailheads = grid.entries.filter { (_, v) -> v == '0' }

    return trailheads.sumOf { (trailhead, v) ->
        allNines(trailhead, grid).distinct().count()
    }
}

fun allNines(trailhead: Vector, grid: Map<Vector, Char>): MutableSet<Vector> {
    val queue = ArrayDeque<Vector>()
    queue.add(trailhead)

    val seen = mutableSetOf(trailhead)

    val result = mutableSetOf<Vector>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val height = grid.getValue(current)

        if (height == '9') {
            result.add(current)
            continue
        }

        grid.allHeadings(current)
            .filter { it.second - height == 1 }
            .map { it.first }
            .filter { it !in seen }
            .also {
                queue.addAll(it)
                seen.addAll(it)
            }
    }

    return result
}

private fun Map<Vector, Char>.allHeadings(location: Vector): List<Pair<Vector, Char>> {
    return Heading.entries.map { heading ->
        location.advance(heading).let { it to getValue(it) }
    }
}

private suspend fun part2(input: String): Int {
    return withContext(Dispatchers.Default) {
        val grid = buildGrid(input.lines()).withDefault { '.' }

        val trailheads = grid.entries.filter { (_, v) -> v == '0' }

        trailheads.map { (trailhead, v) ->
            async {
                allPathsToNines(trailhead, grid).count()
            }
        }.awaitAll().sum()
    }
}

fun allPathsToNines(trailhead: Vector, grid: Map<Vector, Char>): MutableSet<List<Vector>> {
    val pathsInProgress = mutableListOf<MutableList<Vector>>()
    pathsInProgress.add(mutableListOf(trailhead))

    val finishedPaths = mutableSetOf<List<Vector>>()

    while (pathsInProgress.isNotEmpty()) {
        val currentPath = pathsInProgress.removeFirst()
        val currentLocation = currentPath.last()

        val height = grid.getValue(currentLocation)

        if (height == '9') {
            finishedPaths.add(currentPath)
            continue
        }

        grid.allHeadings(currentLocation).filter { it.second - height == 1 }.map { it.first }.forEach {
            pathsInProgress.add(currentPath.toMutableList().apply { add(it) })
        }
    }

    return finishedPaths
}
