package com.sphericalchickens.app.aoc2015gem.day03

import com.sphericalchickens.utils.readInputText

/**
 * # Advent of Code 2015, Day 3: Perfectly Spherical Houses in a Vacuum
 *
 * This program solves a puzzle about calculating the number of unique houses Santa
 * visits on an infinite 2D grid based on a sequence of movement instructions.
 *
 * ## Principles Applied
 * 1.  **Literate Programming**: The code is structured to be read top-down. The `main`
 * function acts as a narrative, guiding the reader through the process of testing
 * the logic and solving both parts of the puzzle.
 * 2.  **Idiomatic Kotlin & Functional Style**:
 * - The stateful `Entity` class has been replaced with a functional approach.
 * - The `scan` function is used to transform a sequence of movement characters into
 * a sequence of coordinates, which is a perfect use case for tracking accumulated state.
 * - For Part 2, `filterIndexed` provides a clean, declarative way to partition the
 * instructions for Santa and Robo-Santa without manual iteration.
 * - A central, reusable helper function, `getVisitedHouses`, encapsulates the core
 * logic of calculating a path from instructions.
 */
fun main() {
    // --- Verification ---
    // We begin by running our logic against the examples from the puzzle description.
    // This validates our approach before we apply it to the full input file.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // The puzzle input is a long string of directional characters. We read this from a
    // file in the project's resources folder.
    val puzzleInput = readInputText("aoc2015/day03_input.txt")
    println("\n--- Advent of Code 2015, Day 3 ---")


    // --- Part 1: Houses Visited by Santa ---
    // We calculate the number of unique houses visited by Santa alone.
    val part1Result = countUniqueHouses(puzzleInput)
    println("🎁 Part 1: $part1Result houses receive at least one present.")


    // --- Part 2: Houses Visited by Santa and Robo-Santa ---
    // We now calculate the number of unique houses visited when Santa and Robo-Santa
    // take turns following the instructions.
    val part2Result = countUniqueHousesWithRoboSanta(puzzleInput)
    println("🤖 Part 2: $part2Result houses receive at least one present with Robo-Santa's help.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * A simple data class to represent a coordinate on the 2D grid.
 * Overloading the `plus` operator allows for a clean and readable syntax for movement.
 */
private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) =
        Vector(x + other.x, y + other.y)
}

/**
 * Translates a movement character ('^', 'v', '<', '>') into a `Vector` representing
 * the change in coordinates.
 */
private fun Char.toDirectionVector(): Vector {
    return when (this) {
        '^' -> Vector(0, -1) // North
        'v' -> Vector(0, 1)  // South
        '<' -> Vector(-1, 0) // West
        '>' -> Vector(1, 0)  // East
        else -> error("Unknown movement character: $this")
    }
}

/**
 * The main workhorse function. It takes a string of instructions and returns the
 * set of all unique houses (coordinates) that were visited.
 *
 * @param instructions A string of movement characters, e.g., "^>v<".
 * @return A `Set` of `Vector` coordinates representing every unique house visited.
 */
private fun getVisitedHouses(instructions: String): Set<Vector> {
    // `scan` is the ideal tool here. It starts with an initial value (the origin)
    // and applies an operation for each character in the string, yielding the
    // sequence of all intermediate results (the path taken).
    return instructions
        .scan(Vector(0, 0)) { currentLocation, moveChar ->
            currentLocation + moveChar.toDirectionVector()
        }
        .toSet() // Converting the list of all visited locations to a Set gives us the unique ones.
}

/**
 * ## Part 1: Calculates the number of unique houses visited by a single delivery person.
 */
private fun countUniqueHouses(instructions: String): Int {
    return getVisitedHouses(instructions).size
}

/**
 * ## Part 2: Calculates the number of unique houses visited by Santa and Robo-Santa.
 */
private fun countUniqueHousesWithRoboSanta(instructions: String): Int {
    // 1. Partition the instructions into two groups based on the index.
    val (evenMoves, oddMoves) = instructions
        .withIndex() // Create an iterable of (index, char) pairs
        .partition { (index, _) -> index % 2 == 0 } // Split into even and odd indices

    // 2. Convert the lists of (index, char) pairs back into strings.
    val santaInstructions = evenMoves.map { it.value }.joinToString("")
    val roboSantaInstructions = oddMoves.map { it.value }.joinToString("")

    // 3. The rest of the logic remains the same.
    val santaVisited = getVisitedHouses(santaInstructions)
    val roboSantaVisited = getVisitedHouses(roboSantaInstructions)

    return (santaVisited + roboSantaVisited).size
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    check(countUniqueHouses(">") == 2)
    check(countUniqueHouses("^>v<") == 4)
    check(countUniqueHouses("^v^v^v^v^v") == 2)

    // Part 2 Test Cases
    check(countUniqueHousesWithRoboSanta("^v") == 3)
    check(countUniqueHousesWithRoboSanta("^>v<") == 3)
    check(countUniqueHousesWithRoboSanta("^v^v^v^v^v") == 11)
}