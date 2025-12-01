package com.sphericalchickens.aoc2016gem.day24

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    println("--- Advent of Code 2016, Day 24 (Gemini Refactor) ---")
    
    // Read input using the provided utility
    val input = readInputLines("aoc2016/day24_input.txt")

    // Run verification tests
    runTests()

    // Part 1 Execution
    println("🎁 Solving Part 1...")
    val time1 = measureTimeMillis {
        val result = solve(input, returnToStart = false)
        println("   Part 1: $result")
    }
    println("   Runtime: ${time1}ms")

    // Part 2 Execution
    println("🎀 Solving Part 2...")
    val time2 = measureTimeMillis {
        val result = solve(input, returnToStart = true)
        println("   Part 2: $result")
    }
    println("   Runtime: ${time2}ms")
}

private fun runTests() {
    println("🧪 Running Tests...")
    val testInput = """
        ###########
        #0.1.....2#
        #.#######.#
        #4.......3#
        ###########
    """.trimIndent().lines()

    // Test Part 1 logic with example data
    check("Part 1 Example", 14, solve(testInput, returnToStart = false))
    println("✅ All tests passed!")
}

/**
 * Coordinate class for grid navigation.
 */
private data class Point(val x: Int, val y: Int) {
    fun neighbors(): List<Point> = listOf(
        Point(x, y - 1),
        Point(x, y + 1),
        Point(x - 1, y),
        Point(x + 1, y)
    )
}

/**
 * Solves the Traveling Salesperson Problem on the grid.
 *
 * @param grid The string representation of the map.
 * @param returnToStart Boolean flag to indicate if the robot must return to '0' (Part 2).
 * @return The minimum number of steps required.
 */
private fun solve(grid: List<String>, returnToStart: Boolean): Int {
    // 1. Locate all POIs (0-9)
    val locations = findLocations(grid)
    
    // 2. Pre-calculate shortest paths between all pairs of POIs (Adjacency Matrix)
    val graph = buildAdjacencyGraph(grid, locations)

    // 3. Generate permutations and find the minimum path cost
    // We always start at '0', so we only permute the other locations.
    val locationsToVisit = locations.keys.filter { it != 0 }

    return permutations(locationsToVisit).minOf { path ->
        calculatePathCost(path, graph, returnToStart)
    }
}

/**
 * Scans the grid to map the digits '0'-'9' to their x,y coordinates.
 */
private fun findLocations(grid: List<String>): Map<Int, Point> {
    val locations = mutableMapOf<Int, Point>()
    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            if (char.isDigit()) {
                locations[char.digitToInt()] = Point(x, y)
            }
        }
    }
    return locations
}

/**
 * Builds a graph where keys are POI IDs and values are maps of (Target POI ID -> Distance).
 */
private fun buildAdjacencyGraph(grid: List<String>, locations: Map<Int, Point>): Map<Int, Map<Int, Int>> {
    // For every POI, run a BFS to find distances to all other POIs
    return locations.mapValues { (_, startPoint) ->
        bfs(startPoint, grid, locations)
    }
}

/**
 * Performs a BFS from a starting point to find the shortest distances to all other target points.
 */
private fun bfs(start: Point, grid: List<String>, targets: Map<Int, Point>): Map<Int, Int> {
    val distances = mutableMapOf<Point, Int>()
    val queue = ArrayDeque<Point>()
    
    distances[start] = 0
    queue.add(start)

    // Optimization: Track which targets we still need to find to exit early
    val pendingTargets = targets.values.toMutableSet()
    pendingTargets.remove(start)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentDist = distances.getValue(current)

        // Check if we hit a target
        if (current in pendingTargets) {
            pendingTargets.remove(current)
            if (pendingTargets.isEmpty()) break // Found all relevant points
        }

        for (neighbor in current.neighbors()) {
            // Check bounds, walls, and visited status
            if (neighbor.y in grid.indices && 
                neighbor.x in grid[neighbor.y].indices && 
                grid[neighbor.y][neighbor.x] != '#' &&
                neighbor !in distances
            ) {
                distances[neighbor] = currentDist + 1
                queue.add(neighbor)
            }
        }
    }

    // Map the Point results back to their Int IDs
    return targets.mapNotNull { (id, point) ->
        distances[point]?.let { dist -> id to dist }
    }.toMap()
}

/**
 * Calculates the total cost of a specific permutation of visits.
 * @param path The order of nodes to visit (excluding start '0').
 * @param graph The pre-computed adjacency matrix of distances.
 * @param returnToStart Whether to add the cost of returning to '0' at the end.
 */
private fun calculatePathCost(
    path: List<Int>, 
    graph: Map<Int, Map<Int, Int>>, 
    returnToStart: Boolean
): Int {
    var current = 0 // Always start at 0
    var cost = 0
    
    for (next in path) {
        cost += graph[current]?.get(next) 
            ?: error("No path found between $current and $next")
        current = next
    }
    
    if (returnToStart) {
        cost += graph[current]?.get(0) 
            ?: error("No path found between $current and 0")
    }
    
    return cost
}

/**
 * Generates all permutations of a list using a Sequence.
 * This is memory efficient and allows for lazy evaluation.
 */
private fun <T> permutations(list: List<T>): Sequence<List<T>> = sequence {
    if (list.isEmpty()) {
        yield(emptyList())
    } else {
        for (i in list.indices) {
            val element = list[i]
            val remaining = list.take(i) + list.drop(i + 1)
            for (perm in permutations(remaining)) {
                yield(listOf(element) + perm)
            }
        }
    }
}