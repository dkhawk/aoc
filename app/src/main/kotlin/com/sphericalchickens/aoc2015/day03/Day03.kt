package com.sphericalchickens.aoc2015.day03

import com.sphericalchickens.utils.readInputText

/**
 * # Advent of Code 2015, Day 03: TBD
 *
 * This program solves the puzzle for Day 03.
 *
 * > delivers presents to 2 houses: one at the starting location, and one to the east.
 *
 * ^>v< delivers presents to 4 houses in a square, including twice to the house at his starting/ending location.
 *
 * ^v^v^v^v^v delivers a bunch of presents to some very lucky children at only 2 houses.
 *
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputText("aoc2015/day03_input.txt")
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

private data class Vector(val x: Int, val y: Int) {
    fun move(command: Char): Vector {
        return when (command) {
            '^' -> this + Vector(0, -1)
            'v' -> this + Vector(0, 1)
            '<' -> this + Vector(-1, 0)
            '>' -> this + Vector(1, 0)
            else -> error("Unknown movement character: $command")
        }
    }

    private operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
}

private class Entity() {
    var location = Vector(0, 0)
    val visited = mutableSetOf(location)

    fun move(c: Char) {
        location = location.move(c)
        visited.add(location)
    }

    fun totalVisited() = visited.size
}

fun part1(input: String): Int {
    val santa = Entity()
    input.forEach { santa.move(it) }
    return santa.totalVisited()
}


fun part2(input: String): Int {
    val santa = Entity()
    val robot = Entity()

    val moves = input.iterator()

    while (moves.hasNext()) {
        santa.move(moves.nextChar())
        if (moves.hasNext()) robot.move(moves.nextChar())
    }

    return (santa.visited + robot.visited).size
}


// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    check(part1(">") == 2)
    check(part1("^>v<") == 4)
    check(part1("^v^v^v^v^v") == 2)

    // Part 2 Test Cases
    check(part2("^v") == 3)
    check(part2("^>v<") == 3)
    check(part2("^v^v^v^v^v") == 11)
}
