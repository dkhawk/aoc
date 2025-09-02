package com.sphericalchickens.aoc2016gem.day01b

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs

/**
 * This file contains a highly idiomatic and efficient Kotlin solution for the Advent of Code 2016, Day 1 puzzle.
 * The solution is designed to be clean, readable, and performant, showcasing modern Kotlin features.
 *
 * The code is written in a literate programming style, where the code and its explanation are interwoven.
 * This makes the code easier to understand, not just as a set of instructions for the computer, but as a
 * narrative for human readers.
 */

/**
 * The main entry point of the program. It orchestrates the execution of the solution,
 * including running tests and printing the final results.
 */
fun main() {
    println("--- Advent of Code 2016, Day 1 (Gemini's Own Solution) ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day01_input.txt").first()
    val (part1, part2) = solve(input)

    println("🎁 Part 1: $part1")
    println("🎀 Part 2: $part2")
}

/**
 * This is the heart of the solution. It takes the raw input string and returns a Pair of integers,
 * representing the solutions to Part 1 and Part 2 of the puzzle.
 *
 * The key design decision here is to solve both parts in a single pass over the instructions.
 * This is more efficient than processing the instructions twice.
 *
 * The function first parses the input string into a sequence of `Instruction`s.
 * Then, it uses the `fold` function to process the instructions. `fold` is a powerful
 * functional programming tool that allows us to accumulate a state over a sequence.
 * In this case, the state is represented by the `State` data class.
 *
 * After processing all instructions, the final state contains everything we need to calculate
 * the answers for both parts.
 */
fun solve(input: String): Pair<Int, Int> {
    val instructions = parseInstructions(input)

    val finalState = instructions.fold(State.initial) { state, instruction ->
        state.next(instruction)
    }

    val part1 = finalState.currentPosition.distanceTo(Position.origin)
    val part2 = finalState.firstRevisitedLocation?.distanceTo(Position.origin.location) ?: 0

    return part1 to part2
}

/**
 * A simple helper function to parse the raw input string into a lazy `Sequence` of `Instruction` objects.
 * Using a `Sequence` is a good practice in Kotlin for processing large collections, as it avoids
 * creating intermediate collections.
 */
private fun parseInstructions(input: String): Sequence<Instruction> = sequence {
    input.split(", ").forEach { instructionString ->
        val direction = instructionString.first()
        val distance = instructionString.drop(1).toInt()
        yield(Instruction(direction, distance))
    }
}

/**
 * This data class represents the state of our simulation at any given point.
 * It's an immutable data class, which is a core concept of functional programming.
 * Each time we process an instruction, we create a new `State` object, rather than
 * modifying the existing one. This makes the code safer and easier to reason about.
 *
 * @param currentPosition The current position and heading on the grid.
 * @param visitedLocations A set of all the grid locations that have been visited so far.
 * @param firstRevisitedLocation The first location that was visited twice. It's nullable because
 * we might not have found a revisited location yet.
 */
private data class State(
    val currentPosition: Position,
    val visitedLocations: Set<Vector>,
    val firstRevisitedLocation: Vector?
) {
    companion object {
        /**
         * The initial state of the simulation, at the origin.
         */
        val initial = State(Position.origin, setOf(Position.origin.location), null)
    }

    /**
     * This function is the core of the state transition logic. It takes the current state
     * and an instruction, and returns the new state after applying the instruction.
     */
    fun next(instruction: Instruction): State {
        val newPosition = currentPosition.turn(instruction.direction)
        var updatedRevisitedLocation = firstRevisitedLocation

        val path = newPosition.path(instruction.distance).toList()

        if (updatedRevisitedLocation == null) {
            val revisited = path.firstOrNull { visitedLocations.contains(it) }
            if (revisited != null) {
                updatedRevisitedLocation = revisited
            }
        }

        val newVisited = visitedLocations + path

        return State(
            currentPosition = newPosition.advance(instruction.distance),
            visitedLocations = newVisited,
            firstRevisitedLocation = updatedRevisitedLocation
        )
    }
}

/**
 * Represents a position on the grid, with a location and a heading.
 */
private data class Position(val location: Vector, val heading: Heading) {
    companion object {
        val origin = Position(Vector(0, 0), Heading.NORTH)
    }

    fun turn(direction: Char) = copy(heading = heading.turn(direction))

    fun advance(distance: Int) = copy(location = location + heading.vector * distance)

    fun distanceTo(other: Position) = location.distanceTo(other.location)

    /**
     * Generates a sequence of all the locations visited when moving a certain distance.
     */
    fun path(distance: Int): Sequence<Vector> = sequence {
        var current = location
        repeat(distance) {
            current += heading.vector
            yield(current)
        }
    }
}

/**
 * A simple 2D vector.
 */
private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun times(scalar: Int) = Vector(x * scalar, y * scalar)
    fun distanceTo(other: Vector) = abs(x - other.x) + abs(y - other.y)
}

/**
 * The four cardinal directions.
 */
private enum class Heading(val vector: Vector) {
    NORTH(Vector(0, -1)),
    EAST(Vector(1, 0)),
    SOUTH(Vector(0, 1)),
    WEST(Vector(-1, 0));

    fun turn(direction: Char): Heading {
        val newOrdinal = when (direction) {
            'R' -> (ordinal + 1) % entries.size
            'L' -> (ordinal + entries.size - 1) % entries.size
            else -> error("Invalid direction: $direction")
        }
        return entries[newOrdinal]
    }
}

/**
 * A single instruction from the input.
 */
private data class Instruction(val direction: Char, val distance: Int)

/**
 * A set of tests to ensure the correctness of the solution.
 */
private fun runTests() {
    check("R2, L3", 5 to 0, solve("R2, L3"))
    check("R2, R2, R2", 2 to 0, solve("R2, R2, R2"))
    check("R5, L5, R5, R3", 12 to 0, solve("R5, L5, R5, R3"))
    check("R8, R4, R4, R8", 8 to 4, solve("R8, R4, R4, R8"))
}