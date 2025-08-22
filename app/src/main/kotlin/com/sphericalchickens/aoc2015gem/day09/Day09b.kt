package com.sphericalchickens.aoc2015gem.day09

import com.sphericalchickens.utils.readInputLines
import kotlin.math.max
import kotlin.math.min

/**
 * # Advent of Code 2015, Day 9: All in a Single Night (DP Version)
 *
 * This program solves the Traveling Salesperson Problem (TSP) for the Day 9 puzzle
 * using a more efficient approach: **Dynamic Programming with Bitmasking**.
 *
 * ## Approach
 * 1.  **Parse & Map**: The input is parsed into an adjacency matrix `distMatrix` for
 * O(1) distance lookups. City names are mapped to integer indices (0 to N-1) to
 * work with array indices and bitmasks.
 * 2.  **DP State**: We use a memoization table, `memo[mask][lastCity]`, to store the
 * optimal distance for a subproblem.
 * - `mask`: An integer where the i-th bit is 1 if city `i` has been visited.
 * - `lastCity`: The index of the last city visited in this sub-path.
 * 3.  **Recursive Solver**: A recursive function explores paths. It takes the current
 * mask and the last visited city as parameters. It calculates the optimal path by
 * trying all possible *next* unvisited cities, recursively calling itself, and
 * memoizing the result.
 * 4.  **Find Extrema**: The main function initiates the DP process for every city as a
 * potential starting point and then finds the overall minimum (Part 1) and
 * maximum (Part 2) path lengths from the results.
 *
 * ## Complexity
 * - Time: O(N² * 2ⁿ) - A massive improvement over the O(N!) brute-force approach.
 * - Space: O(N * 2ⁿ) - To store the memoization table.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day09_input.txt")
    println("\n--- Advent of Code 2015, Day 9 (DP Version) ---")

    // --- Problem Analysis ---
    // Parse the input into a graph representation suitable for the DP algorithm.
    val (distMatrix, cityMap) = parseDistancesToMatrix(puzzleInput)
    val numCities = cityMap.size

    // --- Part 1: Shortest Route ---
    val shortestDistance = solveTsp(distMatrix, numCities, findMin = true)
    println("📏 Part 1: The shortest route is $shortestDistance.")

    // --- Part 2: Longest Route ---
    val longestDistance = solveTsp(distMatrix, numCities, findMin = false)
    println("📐 Part 2: The longest route is $longestDistance.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * Solves the TSP problem for the given distance matrix.
 *
 * @param distMatrix An adjacency matrix where `distMatrix[i][j]` is the distance between city i and j.
 * @param numCities The total number of cities.
 * @param findMin If true, finds the shortest path (minimization). If false, finds the longest path (maximization).
 * @return The length of the optimal path found.
 */
private fun solveTsp(distMatrix: Array<IntArray>, numCities: Int, findMin: Boolean): Int {
    // The final mask represents visiting all cities (all N bits are set to 1).
    val finalMask = (1 shl numCities) - 1
    // Memoization table: memo[mask][lastCity]
    val memo = Array(1 shl numCities) { IntArray(numCities) { -1 } }

    var optimalPath = if (findMin) Int.MAX_VALUE else Int.MIN_VALUE

    // We must try starting the tour from each city, as the problem isn't a cycle.
    for (startCity in 0 until numCities) {
        val pathCost = tspRecursive(startCity, 1 shl startCity, finalMask, distMatrix, numCities, memo, findMin)
        optimalPath = if (findMin) min(optimalPath, pathCost) else max(optimalPath, pathCost)
    }

    return optimalPath
}

/**
 * The recursive solver for the TSP using memoization.
 *
 * @param lastCity The index of the last city visited in the current path.
 * @param mask The bitmask representing the set of visited cities.
 * @param finalMask The target mask where all cities have been visited.
 * @param distMatrix The adjacency matrix of distances.
 * @param numCities The total number of cities.
 * @param memo The memoization table to store results of subproblems.
 * @param findMin A boolean to switch between minimization and maximization.
 * @return The optimal path length for the current subproblem.
 */
private fun tspRecursive(
    lastCity: Int,
    mask: Int,
    finalMask: Int,
    distMatrix: Array<IntArray>,
    numCities: Int,
    memo: Array<IntArray>,
    findMin: Boolean
): Int {
    // Base Case: If all cities have been visited, the path is complete. Return 0.
    if (mask == finalMask) {
        return 0
    }

    // If this subproblem has already been solved, return the stored result.
    if (memo[mask][lastCity] != -1) {
        return memo[mask][lastCity]
    }

    var optimalVal = if (findMin) Int.MAX_VALUE else Int.MIN_VALUE

    // Iterate through all cities to find the next one to visit.
    for (nextCity in 0 until numCities) {
        // Check if the nextCity has NOT been visited yet (i-th bit is 0).
        if ((mask and (1 shl nextCity)) == 0) {
            // Mark the nextCity as visited by updating the mask.
            val newMask = mask or (1 shl nextCity)

            // Recursively find the optimal path from the nextCity.
            val subPathCost = tspRecursive(nextCity, newMask, finalMask, distMatrix, numCities, memo, findMin)

            // If the sub-path is valid (not max/min value), add the current leg's distance.
            if (subPathCost != Int.MAX_VALUE && subPathCost != Int.MIN_VALUE) {
                val currentPathCost = distMatrix[lastCity][nextCity] + subPathCost
                optimalVal = if (findMin) min(optimalVal, currentPathCost) else max(optimalVal, currentPathCost)
            }
        }
    }

    // Store the result in the memoization table before returning.
    memo[mask][lastCity] = optimalVal
    return optimalVal
}


/**
 * Parses input lines into an adjacency matrix and a city-to-index map.
 *
 * @param input A list of strings, e.g., "London to Dublin = 464".
 * @return A Pair containing the adjacency matrix and the city-to-index map.
 */
private fun parseDistancesToMatrix(input: List<String>): Pair<Array<IntArray>, Map<String, Int>> {
    val edgeRegex = """(\w+) to (\w+) = (\d+)""".toRegex()
    val distances = mutableMapOf<Pair<String, String>, Int>()
    val cities = mutableSetOf<String>()

    input.forEach { line ->
        val match = edgeRegex.find(line) ?: error("Invalid input format: $line")
        val (city1, city2, distStr) = match.destructured
        val distance = distStr.toInt()
        cities.add(city1)
        cities.add(city2)
        distances[city1 to city2] = distance
        distances[city2 to city1] = distance
    }

    val cityList = cities.toList()
    val cityMap = cityList.withIndex().associate { (index, name) -> name to index }
    val numCities = cities.size
    val distMatrix = Array(numCities) { IntArray(numCities) }

    for (i in 0 until numCities) {
        for (j in 0 until numCities) {
            if (i != j) {
                val city1 = cityList[i]
                val city2 = cityList[j]
                distMatrix[i][j] = distances[city1 to city2] ?: error("Distance not found between $city1 and $city2")
            }
        }
    }

    return distMatrix to cityMap
}

// ---------------------------------------------------------------------------------------------
// Test Functions
// ---------------------------------------------------------------------------------------------

private fun runTests() {
    val testInput = """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """.trimIndent().lines()

    val (distMatrix, cityMap) = parseDistancesToMatrix(testInput)
    val numCities = cityMap.size

    val shortest = solveTsp(distMatrix, numCities, findMin = true)
    check(shortest == 605) { "Test failed for shortest path. Expected 605, got $shortest" }

    val longest = solveTsp(distMatrix, numCities, findMin = false)
    check(longest == 982) { "Test failed for longest path. Expected 982, got $longest" }
}
