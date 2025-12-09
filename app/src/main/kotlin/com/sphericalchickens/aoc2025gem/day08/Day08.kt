package com.sphericalchickens.aoc2025gem.day08

import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val exampleInput = """
        162,817,812
        57,618,57
        906,360,560
        592,479,940
        352,342,300
        466,668,158
        542,29,236
        431,825,988
        739,650,466
        52,470,668
        216,146,977
        819,987,18
        117,168,530
        805,96,715
        346,949,466
        970,615,88
        941,993,340
        862,61,35
        984,92,344
        425,690,689
    """.trimIndent()

    // Test with the example case (N = 10)
    println("--- Test Case ---")
    val result = solve(exampleInput, 10)
    println("Product of 3 largest circuits: $result")

    // Logic verification
    val expected = 40L
    if (result == expected) {
        println("✅ SUCCESS: Matches expected value ($expected)")
    } else {
        println("❌ FAILED: Expected $expected, got $result")
    }
}

fun solve(input: String, connectionLimit: Int): Long {
    val points = input.lineSequence()
        .filter { it.isNotBlank() }
        .mapIndexed { index, line ->
            val coords = line.split(",").map { it.trim().toInt() }
            Point3D(index, coords[0], coords[1], coords[2])
        }
        .toList()

    // Generate all unique pairs and calculate squared Euclidean distance
    // Using squared distance avoids floating point precision issues during sorting
    val allPairs = ArrayList<Connection>()
    for (i in 0 until points.size) {
        for (j in i + 1 until points.size) {
            val p1 = points[i]
            val p2 = points[j]
            val dx = (p1.x - p2.x).toLong()
            val dy = (p1.y - p2.y).toLong()
            val dz = (p1.z - p2.z).toLong()
            val distSq = dx * dx + dy * dy + dz * dz
            allPairs.add(Connection(p1, p2, distSq))
        }
    }

    // Sort by distance (smallest first)
    allPairs.sortBy { it.distSq }

    // Take exactly the top N pairs, even if they are redundant
    val pairsToProcess = allPairs.take(connectionLimit)

    // Use Disjoint Set Union (DSU) to track circuits
    val dsu = DSU(points.size)

    for (connection in pairsToProcess) {
        // Attempt to connect. DSU handles the "already in same circuit" check internally
        // by simply not changing the root if they are already connected.
        dsu.union(connection.p1.id, connection.p2.id)
    }

    // Calculate circuit sizes
    val circuitSizes = dsu.getClusterSizes()

    // Sort descending to find the largest ones
    val sortedSizes = circuitSizes.sortedDescending()

    // Multiply the three largest
    // Note: If there are fewer than 3 circuits, this logic might need adjustment,
    // but the problem implies at least 3 exist.
    return sortedSizes.take(3).map { it.toLong() }.reduce { acc, i -> acc * i }
}

// --- Data Structures ---

data class Point3D(val id: Int, val x: Int, val y: Int, val z: Int)

data class Connection(val p1: Point3D, val p2: Point3D, val distSq: Long)

class DSU(val size: Int) {
    private val parent = IntArray(size) { it }
    private val rank = IntArray(size) { 0 }

    fun find(i: Int): Int {
        if (parent[i] != i) {
            parent[i] = find(parent[i]) // Path compression
        }
        return parent[i]
    }

    fun union(i: Int, j: Int) {
        val rootI = find(i)
        val rootJ = find(j)

        if (rootI != rootJ) {
            // Union by rank
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI
            } else {
                parent[rootI] = rootJ
                rank[rootJ]++
            }
        }
        // If rootI == rootJ, "nothing happens" (they are already connected)
    }

    fun getClusterSizes(): List<Int> {
        val counts = HashMap<Int, Int>()
        for (i in 0 until size) {
            val root = find(i)
            counts[root] = counts.getOrDefault(root, 0) + 1
        }
        return counts.values.toList()
    }
}