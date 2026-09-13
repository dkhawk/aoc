package com.sphericalchickens.aoc2024.day20

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*

import com.sphericalchickens.utils.Vector
import kotlin.collections.ArrayDeque
import kotlin.time.measureTime

val testInput = """
    ###############
    #...#...#.....#
    #.#.#.#.#.###.#
    #S#...#.#.#...#
    #######.#.#.###
    #######.#.#...#
    #######.#.###.#
    ###..E#...#...#
    ###.#######.###
    #...###...#...#
    #.#####.#.###.#
    #.#...#.#.#...#
    #.#.#.#.#.#.###
    #...#...#...###
    ###############
""".trimIndent().lines()

fun main() {
    check(part1(testInput, 20) == 5)
    check(part2(testInput, 70) == (12 + 22 + 4 + 3))

    val input = readLines("inputs/20")
    part1(input, 100).println()
//    measureTime {
//        part2(input, 100).println()  // 693.109250ms
//    }.println()

    measureTime {
        part2b(input, 100).println()  // 342.174458ms
        // should be 988931
    }.println()

//    measureTime {
//        part2c(input, 100).println()  // 339.796375ms
//        // should be 988931
//    }.println()
}

private fun part1(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val costMap = solve(grid, start, end)

    val cheats = findCheats(costMap, grid)

    val usefulCheats = cheats.filter { it.second > 0 }

    val hist = usefulCheats.groupBy { it.second }.map { (cheat, cheats) -> cheat to cheats.size }

    val answer = hist.fold(0) { acc, cheat ->
        if (cheat.first >= target) acc + cheat.second else acc
    }

    return answer
}

fun findCheats(costMap: Map<Vector, Int>, grid: Map<Vector, Char>): List<Pair<Vector, Int>> {
    return costMap.entries.flatMap { (location, cost) ->
        Heading.entries.map { heading ->
            val other = location + (heading.vector * 2)
            val costOfOther = costMap[other]
            location.advance(heading) to if (costOfOther != null) {
                (cost - costOfOther) - 2  // ??
            } else {
                Int.MIN_VALUE
            }
        }
    }
}

private fun solve(grid: Map<Vector, Char>, start: Vector, end: Vector): MutableMap<Vector, Int> {
    // Solve the maze once to get the cost map

    val visited = mutableMapOf<Vector, Int>()
    visited[start] = 0

    val queue = ArrayDeque<Vector>()
    queue.add(start)

    // I suspect the queue will never grow beyond size 1 for this maze!!
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        val nextCost = visited[current]!! + 1

        for (heading in Heading.entries) {
            val next = current.advance(heading)

            if (next == end) {
                visited[next] = nextCost
                // the queue should already be empty for this kind of maze
                require(queue.isEmpty())
            }

            if (grid[next] != '#') {
                if (nextCost < visited.getOrElse(next, { Int.MAX_VALUE })) {
                    visited[next] = nextCost
                    queue.add(next)
                }
            }
        }
    }

    return visited
}

// Twice as fast with coroutines...
private fun part2b(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val cm = solve(grid, start, end)

    val costToFinish = cm[end]!!

    val costMap = cm.map { it.key to (costToFinish - it.value) }.toMap().withDefault { Int.MAX_VALUE }

    return runBlocking {
        withContext(Dispatchers.Default) {
            costMap.entries.map { (location, cost) ->
                async {
                    allCheats(location, costMap).map { it.second }
                }
            }
        }.awaitAll().flatten().count { it >= target }
    }
}

// Fail.  Don't have time to debug...
private fun part2c(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val cm = solve(grid, start, end)

    val costToFinish = cm[end]!!

    val costMap = cm.map { it.key to (costToFinish - it.value) }.toMap().withDefault { Int.MAX_VALUE }

    val origin = Vector(0, 0)
    val kernel = ((origin - Vector(20, 20))..(origin + Vector(20, 20))).asSequence().flatten()
        .filter { it.manhattanDistanceTo(origin) <= 20 }
        .filterNot { it.manhattanDistanceTo(origin) < 2 }
        .toSet()

//    kernel.map { it to '*' }.toMap().withDefault { '.' }.printGrid().println()

    return runBlocking {
        withContext(Dispatchers.Default) {
            costMap.entries.map { (location, cost) ->
                async {
                    fastCheats(location, costMap, kernel).map { it.second }
                }
            }
        }.awaitAll().flatten().count { it >= target }
    }
}

private fun part2(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val cm = solve(grid, start, end)

    val costToFinish = cm[end]!!

    val costMap = cm.map { it.key to (costToFinish - it.value) }.toMap().withDefault { Int.MAX_VALUE }

    val cheats = findCheats2(costMap)

    return cheats.count { it >= target }
}

fun findCheats2(costMap: Map<Vector, Int>): List<Int> {
    return costMap.entries.flatMap { (location, cost) ->
        allCheats(location, costMap).map { it.second }
    }
}

fun allCheats(location: Vector, costMap: Map<Vector, Int>): List<Pair<Vector, Int>> {
    return ((location - Vector(20, 20))..(location + Vector(20, 20))).asSequence().flatten().filter {
        // The candidate must be on the calculated path
        costMap.containsKey(it)
    }.filter {
        // The candidate must be within cheating range
        location.manhattanDistanceTo(it) <= 20 // Ignore locations that are too far to consider
    }.filter {
        // The candidate much be cheaper than the origin
        costMap.getValue(it) < costMap.getValue(location)  // Ignore locations that are not on the original path or have a higher cost
    }.map { candidate ->
        // Calculate the savings
        val originCost = costMap.getValue(location) - costMap.getValue(candidate)
        val newCost = location.manhattanDistanceTo(candidate)
        candidate to originCost - newCost
    }.toList()
}

fun fastCheats(location: Vector, costMap: Map<Vector, Int>, kernel: Set<Vector>): List<Pair<Vector, Int>> {
    return kernel.intersect(costMap.keys).filter {
        // The candidate must be cheaper than the origin
        costMap.getValue(it) < costMap.getValue(location)  // Ignore locations that are not on the original path or have a higher cost
    }.map { candidate ->
        // Calculate the savings
        val originCost = costMap.getValue(location) - costMap.getValue(candidate)
        val newCost = location.manhattanDistanceTo(candidate)
        candidate to originCost - newCost
    }.toList()
}

