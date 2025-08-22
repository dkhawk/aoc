package com.sphericalchickens.aoc2015gem.day09

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 9: All in a Single Night
 *
 * This program finds the shortest and longest possible routes that visit a set of
 * locations exactly once, a classic computer science problem known as the
 * Traveling Salesperson Problem (TSP).
 *
 * ## Approach
 * 1.  **Parse Input**: The input, which lists distances between pairs of cities, is
 * parsed into a graph data structure. We use an adjacency map (`Map<String, Map<String, Int>>`)
 * for efficient O(1) distance lookups between any two connected cities.
 * 2.  **Generate Routes**: Since the number of cities is small, we can solve this by
 * brute force. We generate every possible unique route (permutation) that visits
 * each city exactly once.
 * 3.  **Calculate Distances**: For each permutation, we calculate the total distance of
 * the route by summing the distances of each leg of the journey.
 * 4.  **Find Extrema**: Finally, we find the minimum and maximum values from our list of
 * total route distances to get the answers for Part 1 and Part 2.
 */
fun main() {
    // --- Verification ---
    // First, we validate our logic against the example provided in the puzzle description.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // We read the puzzle input, which contains all the city-to-city distances.
    val puzzleInput = readInputLines("aoc2015/day09_input.txt")
    println("\n--- Advent of Code 2015, Day 9 ---")

    // --- Problem Analysis ---
    // The input is parsed into a graph representation, and we get a list of all unique cities.
    val (distanceMap, cities) = parseDistances(puzzleInput)

    // We generate all possible routes by finding every permutation of the cities.
    // Then, for each route, we calculate its total distance.
    val allRouteDistances = cities.toList().permutations()
        .map { route -> calculateTotalDistance(route, distanceMap) }
        .toList() // Convert sequence to a list to find both min and max.

    // --- Part 1: Shortest Route ---
    // The shortest route is the minimum value among all calculated route distances.
    val shortestDistance = allRouteDistances.minOrNull() ?: 0
    println("📏 Part 1: The shortest route is $shortestDistance.")

    // --- Part 2: Longest Route ---
    // The longest route is the maximum value.
    val longestDistance = allRouteDistances.maxOrNull() ?: 0
    println("📐 Part 2: The longest route is $longestDistance.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * Parses the input lines into a graph represented by an adjacency map and a set of unique cities.
 *
 * The graph is a map where each key is a source city and its value is another map,
 * containing destination cities and the distance to them. This allows for fast `graph[source][dest]` lookups.
 *
 * @param input A list of strings, e.g., "London to Dublin = 464".
 * @return A Pair containing the distance map and a set of all unique city names.
 */
private fun parseDistances(input: List<String>): Pair<Map<String, Map<String, Int>>, Set<String>> {
    val edgeRegex = """(\w+) to (\w+) = (\d+)""".toRegex()

    // Create a flat list of directed edges, including both A->B and B->A for each input line.
    val directedEdges = input.flatMap { line ->
        val match = edgeRegex.find(line) ?: error("Invalid input format: $line")
        val (source, dest, distStr) = match.destructured
        val distance = distStr.toInt()
        // An undirected edge is represented as two directed edges.
        listOf(
            Triple(source, dest, distance),
            Triple(dest, source, distance)
        )
    }

    val cities = directedEdges.map { it.first }.toSet()

    // Group the edges by their source city and transform the values into a destination->distance map.
    val distanceMap = directedEdges
        .groupBy { it.first } // Key: source city
        .mapValues { (_, edges) ->
            edges.associate { (_, dest, dist) -> dest to dist } // Value: Map<Destination, Distance>
        }

    return distanceMap to cities
}

/**
 * Calculates the total distance of a given route using the distance map.
 *
 * @param route A list of city names in a specific order.
 * @param distanceMap The graph containing distances between cities.
 * @return The integer sum of distances for each leg of the route.
 */
private fun calculateTotalDistance(route: List<String>, distanceMap: Map<String, Map<String, Int>>): Int {
    // `zipWithNext` creates pairs of adjacent cities in the route: (city1, city2), (city2, city3), ...
    return route.zipWithNext { source, destination ->
        // The `!!` asserts that the cities exist in the map, which is safe
        // because the route is generated from the map's keys.
        distanceMap[source]!![destination]!!
    }.sum()
}

/**
 * Generates a sequence of all permutations of the list.
 * This is a lazy operation; permutations are generated as they are requested.
 *
 * @return A Sequence containing all permutations of the list.
 */
private fun <T> List<T>.permutations(): Sequence<List<T>> {
    if (this.isEmpty()) return sequenceOf(emptyList())

    return sequence {
        for (i in this@permutations.indices) {
            val element = this@permutations[i]
            val remaining = this@permutations.subList(0, i) + this@permutations.subList(i + 1, size)
            // Recursively find permutations of the rest and prepend the current element.
            remaining.permutations().forEach { subPermutation ->
                yield(listOf(element) + subPermutation)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """.trimIndent().lines()

    val (distanceMap, cities) = parseDistances(testInput)

    val distances = cities.toList().permutations()
        .map { calculateTotalDistance(it, distanceMap) }
        .toList()

    check(distances.minOrNull() == 605)
    check(distances.maxOrNull() == 982)
}