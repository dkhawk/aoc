package com.sphericalchickens.aoc2024.day18

import com.sphericalchickens.utils.*




val testInput = """
    5,4
    4,2
    4,5
    3,0
    2,1
    6,3
    2,4
    1,5
    0,6
    3,3
    2,6
    5,1
    1,2
    5,5
    2,5
    6,5
    1,4
    0,4
    6,4
    1,1
    6,1
    1,0
    0,5
    1,6
    2,0
""".trimIndent().lines()

fun main() {
    check(part1(testInput) == 22)
    check(part2(testInput) == Vector(6, 1))

    val input = readLines("inputs/18")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

private fun part1(
    input: List<String>
): Int {
    val (size, numBytes) = if (input.size < 30) {
        6 to 12
    } else {
        70 to 1024
    }

    val start = Vector(0, 0)
    val destination = Vector(size, size)

    val bytes = input
        .map { line ->
            line.split(",").map { it.trim().toInt() }.toVector()
        }
        .toCollection(ArrayDeque())

    val bounds = Bounds(start, destination)

    return solve(bounds, bytes, numBytes, start, destination)
}

private fun solve(
    bounds: Bounds,
    bytes: List<Pair<Int, Int>>,
    numBytes: Int,
    start: Vector,
    destination: Vector
): Int {
    val grid = mutableMapOf<Vector, Char>().withDefault { location -> if (bounds.contains(location)) '.' else '#' }
    bytes.take(numBytes).forEach { grid[it.toVector()] = '#' }

    val queue = ArrayDeque<Vector>()
    queue.add(start)

    val visited = mutableMapOf(start to 0)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val cost = visited[current]!!
        val costToNext = cost + 1

        Heading.entries.forEach { heading ->
            val next = current.advance(heading)

            if (grid.getValue(next) == '.') {
                if (next == destination) {
//                    costToNext.println()
                    return costToNext
                }


                val previousCost = visited[next] ?: Int.MAX_VALUE
                if (costToNext < previousCost) {
                    visited[next] = costToNext
                    queue.add(next)
                }
            }
        }
    }

    return -1
}

private fun List<Int>.toVector(): Pair<Int, Int> {
    return this[0] to this[1]
}

private fun <E> List<E>.toPair(): Pair<E, E> {
    return this[0] to this[1]
}

private fun part2(input: List<String>): Vector {
    val (size, numBytes) = if (input.size < 30) {
        6 to 12
    } else {
        70 to 1024
    }

    val start = Vector(0, 0)
    val destination = Vector(size, size)

    val bytes = input
        .map { line ->
            line.split(",").map { it.trim().toInt() }.toVector()
        }
        .toList()

    val bounds = Bounds(start, destination)

    var min = 0
    var max = bytes.size

    var lastSuccess = -1
    var lastFailure = -1

    while (min <= max) {
        val trial = (min + max) / 2

        val result = solve(bounds, bytes, trial, start, destination)

        if (result > 0) {
            lastSuccess = trial
            min = trial
        } else {
            lastFailure = trial
            max = trial
        }

        if (lastSuccess + 1 == lastFailure) {
            break
        }
    }

    "******************".println()
    lastSuccess.println()
    lastFailure.println()

    bytes[lastFailure - 1].println()

    return bytes[lastFailure - 1].toVector()
}