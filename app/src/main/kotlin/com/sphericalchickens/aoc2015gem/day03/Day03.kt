package com.sphericalchickens.aoc2015gem.day03

import com.sphericalchickens.utils.readInputText
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * # Advent of Code 2015, Day 3: Perfectly Spherical Houses in a Vacuum
 *
 * This program solves a puzzle about calculating the number of unique houses Santa
 * visits on an infinite 2D grid based on a sequence of movement instructions.
 *
 * ## Principles Applied
 * 1.  **Literate Programming**: The `main` function acts as a narrative, guiding the
 * reader through the process of testing the logic and solving both parts of the puzzle.
 * 2.  **Concurrency with Coroutines**: For Part 2, we use `async`/`await` to calculate
 * the paths for Santa and Robo-Santa in parallel. This is a modern and efficient
 * way to handle concurrent, independent computations.
 * 3.  **Idiomatic Kotlin**: The code uses `data class`, `scan`, and `partition` to
 * create a solution that is both functional and highly readable.
 */
fun main() = runBlocking { // Use runBlocking to start the main coroutine
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputText("aoc2015/day03_input.txt")
    println("\n--- Advent of Code 2015, Day 3 ---")


    // --- Part 1: Houses Visited by Santa ---
    val part1Result = countUniqueHouses(puzzleInput)
    println("🎁 Part 1: $part1Result houses receive at least one present.")


    // --- Part 2: Houses Visited by Santa and Robo-Santa (in Parallel) ---
    val part2Result = countUniqueHousesWithRoboSantaAsync(puzzleInput)
    println("🤖 Part 2: $part2Result houses receive at least one present with Robo-Santa's help.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
}

private fun Char.toDirectionVector(): Vector {
    return when (this) {
        '^' -> Vector(0, -1)
        'v' -> Vector(0, 1)
        '<' -> Vector(-1, 0)
        '>' -> Vector(1, 0)
        else -> error("Unknown movement character: $this")
    }
}

private fun getVisitedHouses(instructions: String): Set<Vector> {
    return instructions
        .scan(Vector(0, 0)) { currentLocation, moveChar ->
            currentLocation + moveChar.toDirectionVector()
        }
        .toSet()
}

/**
 * ## Part 1: Calculates the number of unique houses visited by a single delivery person.
 */
private fun countUniqueHouses(instructions: String): Int {
    return getVisitedHouses(instructions).size
}

/**
 * ## Part 2: Calculates unique houses visited by Santa and Robo-Santa in parallel.
 *
 * This `suspend` function uses coroutines to perform the two path calculations
 * concurrently. On a multicore system, this can lead to a faster result for
 * large inputs.
 */
private suspend fun countUniqueHousesWithRoboSantaAsync(instructions: String): Int = coroutineScope {
    // 1. Partition the instructions into two groups in a single pass.
    val (evenMoves, oddMoves) = instructions.withIndex()
        .partition { (index, _) -> index % 2 == 0 }

    val santaInstructions = evenMoves.map { it.value }.joinToString("")
    val roboSantaInstructions = oddMoves.map { it.value }.joinToString("")

    // 2. Launch two asynchronous tasks. `async` starts a coroutine and returns a
    // `Deferred` object, which is a promise for the future result.
    val santaVisitedDeferred = async { getVisitedHouses(santaInstructions) }
    val roboSantaVisitedDeferred = async { getVisitedHouses(roboSantaInstructions) }

    // 3. `await()` is called on each Deferred object. This suspends the function
    // until the results are ready, without blocking the thread.
    val santaVisited = santaVisitedDeferred.await()
    val roboSantaVisited = roboSantaVisitedDeferred.await()

    // 4. Once both results are available, combine them and return the size.
    (santaVisited + roboSantaVisited).size
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic. This function is also a coroutine
 * builder to allow it to call our suspend function for Part 2.
 */
private fun runTests() = runBlocking {
    // Part 1 Test Cases
    check(countUniqueHouses(">") == 2)
    check(countUniqueHouses("^>v<") == 4)
    check(countUniqueHouses("^v^v^v^v^v") == 2)

    // Part 2 Test Cases (using the async function)
    check(countUniqueHousesWithRoboSantaAsync("^v") == 3)
    check(countUniqueHousesWithRoboSantaAsync("^>v<") == 3)
    check(countUniqueHousesWithRoboSantaAsync("^v^v^v^v^v") == 11)
}