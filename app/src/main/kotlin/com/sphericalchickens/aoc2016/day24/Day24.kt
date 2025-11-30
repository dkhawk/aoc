package com.sphericalchickens.aoc2016.day24

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 24 ---")

    val input = readInputLines("aoc2016/day24_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input)
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

/*
For example, suppose you have a map like the following:

###########
#0.1.....2#
#.#######.#
#4.......3#
###########
To reach all of the points of interest as quickly as possible, you would have the robot take the following path:

0 to 4 (2 steps)
4 to 1 (4 steps; it can't move diagonally)
1 to 2 (6 steps)
2 to 3 (2 steps)
Since the robot isn't very fast, you need to find it the shortest route. This path is the fewest steps (in the above
example, a total of 14) required to start at 0 and then visit every other location at least once.
 */
private fun runPart1Tests() {
    val testInput = """
        ###########
        #0.1.....2#
        #.#######.#
        #4.......3#
        ###########
    """.trimIndent().lines()

    check("Part 1 Test Case 1", 14, part1(testInput))
}

private fun cheapestTrip(current: Char, costMap: List<Pair<Pair<Char, Char>, Int>>): Int {
    if (costMap.isEmpty()) {
        return 0
    }

    val (nextRoutes, remainingRoutes) = costMap.partition { it.first.first == current || it.first.second == current }

    return nextRoutes.minOfOrNull { next ->
        val cost = next.second  // the cost of this movement
        val (a, b) = next.first
        val dst = if (current == a) b else a
        cost + cheapestTrip(dst, remainingRoutes)
    } ?: error("WTF?")
}

private fun cheapestTrip2(
    current: Char,
    costMap: List<Pair<Pair<Char, Char>, Int>>,
    zeroCosts: List<Pair<Pair<Char, Char>, Int>>
): Int {
    if (costMap.isEmpty()) {
        return zeroCosts.first { it.first.first == current || it.first.second == current }.second
    }

    val (nextRoutes, remainingRoutes) = costMap.partition { it.first.first == current || it.first.second == current }

    return nextRoutes.minOfOrNull { next ->
        val cost = next.second  // the cost of this movement
        val (a, b) = next.first
        val dst = if (current == a) b else a
        cost + cheapestTrip2(dst, remainingRoutes, zeroCosts)
    } ?: error("WTF?")
}

private fun createCostMap(pois: List<Pair<Vector, Char>>, map: Map<Vector, Char>): List<Pair<Pair<Char, Char>, Int>> {
    if (pois.size < 2) {
        return emptyList()
    }

    val src = pois.first()
    val rest = pois.drop(1)
    val myCosts = cost(src, rest, map).map { (key, value) -> (src.second to key) to value }

    return myCosts + createCostMap(rest, map)
}

private fun cost(src: Pair<Vector, Char>, dst: List<Pair<Vector, Char>>, map: Map<Vector, Char>): Map<Char, Int> {
    val costMap = mutableMapOf<Vector, Int>()
    val start = src.first
    val toVisit = dst.map { it.first }.toMutableSet()

    val visited = mutableSetOf<Vector>()
    visited.add(src.first)

    val pq = ArrayDeque<Pair<Vector, Int>>()
    pq.addFirst(start to 0)

    while (toVisit.isNotEmpty() && pq.isNotEmpty()) {
        val (nextLocation, cost) = pq.removeFirst()

        if (toVisit.contains(nextLocation)) {
            costMap[nextLocation] = cost
            toVisit.remove(nextLocation)

            if (toVisit.isEmpty()) {
                return costMap.map { (key, value) -> map.getValue(key) to value }.toMap()
            }
        }

        val nextCost = cost + 1
        nextLocation.getNeighbors().filterNot { visited.contains(it) }.filterNot { map.getValue(it) == '#' }.forEach { vector ->
            pq.addLast(vector to nextCost)
            visited.add(vector)
        }
    }

    error("Failed to find a path from $src to one of the destinations $dst")
}

private data class Vector(val x: Int, val y: Int) {
    fun getNeighbors() : List<Vector> {
        return listOf(
            Vector(x - 1, y),
            Vector(x + 1 , y),
            Vector(x, y - 1),
            Vector(x, y + 1),
        )
    }
}

private fun List<String>.toMaze(): Map<Vector, Char> {
    return this.mapIndexed { row, line ->
        line.mapIndexed { col, ch ->
            Vector(col, row) to ch
        }
    }.flatten().toMap()
}


private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun part1(input: List<String>): Int {
    val map = input.toMaze()
    val pois = map.filterValues { ch -> ch in '0'..'9' }.toList().sortedBy { (vector, ch) -> ch }
    return cheapestTrip('0', createCostMap(pois, map))
}

private fun part2(input: List<String>): Int {
    val map = input.toMaze()
    val pois = map.filterValues { ch -> ch in '0'..'9' }.toList().sortedBy { (vector, ch) -> ch }

    val costMap = createCostMap(pois, map)

    val zeroCosts = costMap.filter { it.first.first == '0' || it.first.second == '0' }

    return cheapestTrip2('0', costMap, zeroCosts)
}
