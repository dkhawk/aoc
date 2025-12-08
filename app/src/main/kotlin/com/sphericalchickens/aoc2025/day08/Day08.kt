package com.sphericalchickens.aoc2025.day08

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

fun main() = runBlocking {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 8 ---")

    val input = readInputLines("aoc2025/day08_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests(this)
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
//            That's not the right answer; your answer is too high: 82348
            part1(this, input, 1000, 3)
        }
        println("   Part 1: $part1Result")
        println("Part 1 runtime: ${formatDuration(part1Duration)}")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            part2(input)
        }
        println("   Part 2: $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}")
    }
}

private suspend fun runPart1Tests(scope: CoroutineScope) {
    val testInput = """
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
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 40, part1(scope, testInput, 10, 3))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private data class Vector(val x: Int, val y: Int, val z: Int) {
    // We only need the ranking.  Sqrt is kind of expensive....
    fun distanceSquared(other: Vector): Long {
        val dx = (x - other.x).toLong()
        val dy = (y - other.y)
        val dz = (z - other.z)

        return (dx * dx + dy * dy + dz * dz)
    }
}

private fun String.toVector() : Vector {
    val (x, y, z) = this.split(",").map { it.trim().toInt() }
    return Vector(x, y, z)
}

private data class Connection(
    val a: Vector,
    val b: Vector,
    val dist: Long
)

private fun distances(jboxes: List<Vector>) : List<Connection> {
    if (jboxes.size < 2) {
        return emptyList()
    }

    val first = jboxes.first()

    return jboxes.drop(1).map { other ->
        Connection(first, other, first.distanceSquared(other))
    }
}

private suspend fun part1(scope: CoroutineScope, input: List<String>, connections: Int, numberCircuits: Int) : Long {
    val jboxes = input.map(String::toVector)

    // oof, n**2
    val jobs = buildList {
        for (i in 0 until jboxes.lastIndex) {
            add(
                scope.async(Dispatchers.Default) {
                    distances(jboxes.slice(i..jboxes.lastIndex))
                }
            )
        }
    }

    val sortedConnections = jobs.awaitAll().flatten().sortedBy { it.dist }

    val circuits = mutableListOf<MutableSet<Vector>>()

    jboxes.forEach {
        circuits.add(mutableSetOf(it))
    }

    var extensionCables = connections
    var i = 0

    while (extensionCables > 0) {
        val connection = sortedConnections[i]
        val aCircuit = circuits.first { circuit -> connection.a in circuit }
        val bCircuit = circuits.first { circuit -> connection.b in circuit }

        if (aCircuit != bCircuit) {
            circuits.remove(bCircuit)
            aCircuit.addAll(bCircuit)
        }
        extensionCables--
        i++
    }

    // Let's verify the circuits are valid
//    jboxes.forEach { jbox ->
//        check("$jbox", 1, circuits.count { jbox in it })
//    }

    return circuits.sortedByDescending { it.size }.take(numberCircuits).fold(1) { acc, ele ->
        acc * ele.size
    }
}

private fun part2(input: List<String>): Int {
    return -1
}
