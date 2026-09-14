package com.sphericalchickens.aoc2024.day08

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.buildGridNoFilter
import kotlinx.coroutines.runBlocking


val testInput = """
    ............
    ........0...
    .....0......
    .......0....
    ....0.......
    ......A.....
    ............
    ............
    ........A...
    .........A..
    ............
    ............
""".trimIndent().lines()

fun main() = runBlocking {
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readLines("inputs/08")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun part1(input: List<String>): Int {
    val (bounds: Bounds, grid) = createGridAndBounds(input)

    val byType = createNodeMap(grid)

    return byType.flatMap { (_, v) ->
        // Brute it!
        combinations(v.toList()).flatMap { combo ->
            antiNodes(combo.first, combo.second)
        }.filter { bounds.contains(it) }
    }.distinct().size
}

fun antiNodes(a: Vector, b: Vector): List<Vector> {
    val delta = b - a
    return listOf((a - delta), (b + delta))
}

fun combinations(nodes: List<Vector>): Sequence<Pair<Vector, Vector>> {
    return sequence {
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                yield(Pair(nodes[i], nodes[j]))
            }
        }
    }
}

fun part2(input: List<String>): Int {
    val (bounds: Bounds, grid) = createGridAndBounds(input)
    val byType = createNodeMap(grid)

    return byType.flatMap { (_, v) ->
        // Brute it!
        combinations(v.toList()).flatMap { combo ->
            antiNodes2(combo.first, combo.second, bounds).toList()
        }
    }.distinct().size
}

fun antiNodes2(a: Vector, b: Vector, bounds: Bounds): List<Vector> {
    val delta = b - a

    return buildList {
        add(a)
        var next = a - delta
        while (bounds.contains(next)) {
            add(next)
            next -= delta
        }

        next = a + delta
        while (bounds.contains(next)) {
            add(next)
            next += delta
        }
    }
}

private fun createGridAndBounds(input: List<String>): Pair<Bounds, Map<Vector, Char>> {
    val bounds: Bounds

    val grid = buildGridNoFilter(input).apply {
        bounds = keys.minMax()
    }.filter { it.value != '.' }.withDefault { '.' }
    return Pair(bounds, grid)
}

private fun createNodeMap(grid: Map<Vector, Char>): MutableMap<Char, MutableSet<Vector>> {
    val byType = mutableMapOf<Char, MutableSet<Vector>>()
    grid.entries.forEach { (k, v) ->
        byType.getOrPut(v) { mutableSetOf() }.add(k)
    }
    return byType
}
