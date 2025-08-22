package com.sphericalchickens.app.aoc2015.day09

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day XX: TBD
 *
 * This program solves the puzzle for Day XX.
 *
 * For example, given the following distances:
 *
 * London to Dublin = 464
 * London to Belfast = 518
 * Dublin to Belfast = 141
 * The possible routes are therefore:
 *
 * Dublin -> London -> Belfast = 982
 * London -> Dublin -> Belfast = 605
 * London -> Belfast -> Dublin = 659
 * Dublin -> Belfast -> London = 659
 * Belfast -> Dublin -> London = 605
 * Belfast -> London -> Dublin = 982
 *
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day09_input.txt")
    println("\n--- Advent of Code 2015, Day XX ---")


    // --- Part 1: TBD ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")


    // --- Part 2: TBD ---
    val part2Result = part2(puzzleInput)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {
    val edges = input.map { it.toEdge() }
    val graph = createGraph(edges)

    val p = graph.keys.toList().permutations() // getPermutations(graph.keys.toList())
    return p.map {
        it.toDistance(graph).sum()
    }.min()
}

fun part2(input: List<String>): Int {
    val edges = input.map { it.toEdge() }
    val graph = createGraph(edges)

    val p = graph.keys.toList().permutations() // getPermutations(graph.keys.toList())
    return p.map {
        it.toDistance(graph).sum()
    }.max()
}

private data class Edge(val source: String, val destination: String, val distance: Int)

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    val testInput1 = """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """.trimIndent().lines()

    check(
        testInput1[0].toEdge() == Edge("London", "Dublin", 464)
    )
}

private fun List<String>.toDistance(graph: Map<String, List<Pair<String, Int>>>): List<Int> {
    return this.zipWithNext { src, dst ->
        graph.getValue(src).first { it.first == dst }.second
    }
}

private fun getPermutations(cities: List<String>): List<String> {
    if (cities.size == 1) {
        return listOf(cities.first())
    }

    val rest = getPermutations(cities.drop(1))

    return rest.map { others ->
        others + cities.first()
    }
}

private val edgeRegex = Regex("""(?<source>\w+)\s+to\s+(?<destination>\w+)\s+=\s+(?<distance>\d+)""")

private fun String.toEdge() : Edge {
    return edgeRegex.matchEntire(this)?.let {
        Edge(
            source = it.groups["source"]!!.value,
            destination = it.groups["destination"]!!.value,
            distance = it.groups["distance"]!!.value.toInt(),
        )
    } ?: error("Invalid format: $this")
}

private fun createGraph(edges: List<Edge>): MutableMap<String, MutableList<Pair<String, Int>>> {
    val graph = mutableMapOf<String, MutableList<Pair<String, Int>>>()

    fun addEdge(source: String, destination: String, distance: Int) {
        graph.getOrPut(source) {
            mutableListOf()
        }.add(destination to distance)
    }

    edges.forEach { edge ->
        addEdge(
            edge.source,
            edge.destination,
            edge.distance
        )
        addEdge(
            edge.destination,
            edge.source,
            edge.distance
        )
    }

    return graph
}

/**
 * Generates a sequence of all permutations of the list.
 *
 * @return A Sequence containing all permutations of the list.
 *
 * Example: `listOf(1, 2, 3).permutations()` will produce `[1,2,3], [1,3,2], [2,1,3], ...`
 */
private fun <T> List<T>.permutations(): Sequence<List<T>> {
    // Base case: If the list is empty, there's only one permutation: an empty list.
    if (this.isEmpty()) {
        return sequenceOf(emptyList())
    }

    val list = this // Store the original list to avoid shadowing

    // The core of the algorithm is recursive and lazy.
    return sequence {
        // For each element in the list...
        for (i in list.indices) {
            val element = list[i]
            // Create a list of the remaining elements.
            val remaining = list.subList(0, i) + list.subList(i + 1, list.size)

            // Recursively find all permutations of the remaining elements.
            val permutationsOfRest = remaining.permutations()

            // For each permutation of the rest, prepend the current element.
            permutationsOfRest.forEach { subPermutation ->
                yield(listOf(element) + subPermutation)
            }
        }
    }
}