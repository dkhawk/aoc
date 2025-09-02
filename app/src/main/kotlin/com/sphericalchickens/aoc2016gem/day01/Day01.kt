package com.sphericalchickens.aoc2016gem.day01

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs

/**
 * This file contains the solution for the Advent of Code 2016, Day 1 puzzle.
 * The puzzle involves navigating a 2D grid based on a series of instructions.
 * The solution is written in a literate programming style, with narrative explanations
 * interleaved with the code.
 */

/**
 * The main entry point of the program.
 * It runs the tests, then reads the input and prints the solutions for both parts.
 */
fun main() {
    println("--- Advent of Code 2016, Day 1 (Gemini) ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day01_input.txt")

    val part1Result = part1(input.first())
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input.first())
    println("🎀 Part 2: $part2Result")
}

/**
 * The starting position for the navigation, at the origin (0,0) and facing North.
 */
private val origin = Position(Vector(0, 0), Heading.NORTH)

/**
 * ## Part 1: Calculating the shortest path
 *
 * Part 1 of the puzzle asks for the shortest distance from the starting point to the final destination.
 * The distance is measured using the Manhattan distance (sum of absolute differences of coordinates).
 *
 * This function takes the input string, parses it into a sequence of instructions, and then
 * uses the `fold` function to apply each instruction to the current position, starting from the `origin`.
 * The `fold` function is a powerful functional tool that accumulates a value over a sequence.
 * In this case, it accumulates the `Position` after each instruction.
 *
 * Finally, it calculates the Manhattan distance from the final position back to the `origin`.
 *
 * This approach is a great example of idiomatic Kotlin, using functional programming concepts
 * to create a concise and expressive solution.
 */
fun part1(input: String): Int {
    return inputSequence(input).fold(origin) { acc, instruction ->
        instruction(acc)
    }.distanceTo(origin)
}

/**
 * This function parses the input string into a `Sequence` of `Instruction` objects.
 * A `Sequence` is used here for efficiency. It processes the elements lazily,
 * which can be beneficial for large inputs, although in this case the input is small.
 *
 * The input string is split by ", ", and for each part, the first character is taken as the
 * direction and the rest of the string is converted to an integer for the distance.
 */
private fun inputSequence(input: String): Sequence<Instruction> = sequence {
    input.split(", ").forEach { inst ->
        val direction = inst.first()
        val distance = inst.drop(1).toInt()
        yield(Instruction(direction, distance))
    }
}

/**
 * Represents a single instruction, consisting of a direction to turn ('L' or 'R')
 * and a distance to travel.
 *
 * The `invoke` operator is overloaded to allow an `Instruction` object to be called as a function.
 * This makes the `fold` operation in `part1` more readable: `instruction(acc)`.
 * When an instruction is "called", it returns a new `Position` after turning and advancing.
 */
private data class Instruction(
    val direction: Char,
    val distance: Int
) {
    operator fun invoke(position: Position): Position {
        return position.turn(direction).advance(distance)
    }
}

/**
 * A simple data class to represent a 2D vector with integer coordinates.
 * It overloads the `plus` and `times` operators for vector addition and scalar multiplication,
 * which makes the vector operations in the `Position` class more natural and readable.
 */
private data class Vector(
    val x: Int,
    val y: Int
) {
    operator fun times(scaler: Int) = Vector(
        x * scaler,
        y * scaler
    )

    operator fun plus(other: Vector): Vector = Vector(
        x + other.x,
        y + other.y
    )

    /**
     * Calculates the Manhattan distance between two vectors.
     */
    fun distanceTo(other: Vector): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

/**
 * An enum to represent the four cardinal directions.
 * Each heading has an associated `Vector` that represents the direction of movement.
 * The `turn` function calculates the new heading after a left or right turn.
 * The use of the `ordinal` property and the modulo operator provides a concise way
 * to cycle through the headings.
 */
private enum class Heading(val vector: Vector) {
    NORTH(Vector(0, -1)),
    EAST(Vector(1, 0)),
    SOUTH(Vector(0, 1)),
    WEST(Vector(-1, 0));

    fun turn(direction: Char): Heading {
        return when (direction) {
            'R' -> entries[(this.ordinal + 1) % entries.size]
            'L' -> entries[(this.ordinal + (entries.size - 1)) % entries.size]
            else -> error("Invalid direction: $direction")
        }
    }
}

/**
 * Represents the current position on the grid, including the location (`Vector`)
 * and the current `Heading`.
 *
 * The functions `turn` and `advance` are designed to be immutable. They return a new
 * `Position` object instead of modifying the existing one. This is a key principle
 * of functional programming that makes the code safer and easier to reason about.
 */
private data class Position(
    val location: Vector,
    val heading: Heading
) {
    fun turn(direction: Char) = copy(heading = heading.turn(direction))
    fun advance(distance: Int): Position {
        return Position(
            location = location + (heading.vector * distance),
            heading = heading
        )
    }

    fun distanceTo(loc: Position): Int {
        return location.distanceTo(loc.location)
    }
}

/**
 * ## Part 2: Finding the first location visited twice
 *
 * Part 2 of the puzzle asks for the first location that is visited twice.
 * This requires keeping track of all visited locations.
 *
 * The original imperative implementation of this part was refactored to be more
 * functional and declarative, aligning with the style of `part1`.
 *
 * This function now works by generating a `Sequence` of all positions visited, one step at a time.
 * It then maps this sequence to a sequence of `Vector` locations and uses the `firstDuplicate`
 * extension function to find the first location that appears more than once.
 *
 * Finally, it calculates the Manhattan distance of this first duplicated location from the origin.
 * This approach is more readable and expresses the intent of the code more clearly.
 */
fun part2(input: String): Int {
    val instructions = inputSequence(input).toList()
    val positions = sequence {
        var currentPosition = origin
        yield(currentPosition)
        for (instruction in instructions) {
            currentPosition = currentPosition.turn(instruction.direction)
            for (step in 1..instruction.distance) {
                currentPosition = currentPosition.advance(1)
                yield(currentPosition)
            }
        }
    }

    return positions
        .map { it.location }
        .firstDuplicate()
        .let { origin.location.distanceTo(it) }
}

/**
 * A generic extension function for `Sequence` that finds the first element
 * that is a duplicate in the sequence.
 *
 * It uses a `mutableSetOf` to keep track of the elements it has seen so far.
 * The `add` method of a `Set` returns `false` if the element is already in the set,
 * which provides a concise way to find the first duplicate.
 */
private fun <T> Sequence<T>.firstDuplicate(): T {
    val seen = mutableSetOf<T>()
    return first { !seen.add(it) }
}

/**
 * A set of tests to verify the correctness of the solution.
 * These tests cover the examples given in the puzzle description for both parts,
 * as well as the turning logic of the `Heading` enum.
 */
private fun runTests() {
    val testInputs = """
        R2, L3 -> 5
        R2, R2, R2 -> 2
        R5, L5, R5, R3 -> 12
    """.trimIndent().lines().map {
        val p = it.split("->")
        val input = p.first().trim()
        val expected = p.last().trim().toInt()

        input to expected
    }

    for (testCase in testInputs) {
        check("Part 1: $testCase", testCase.second, part1(testCase.first))
    }

    check("N R E", Heading.EAST, Heading.NORTH.turn('R'))
    check("E R S", Heading.SOUTH, Heading.EAST.turn('R'))
    check("S R W", Heading.WEST, Heading.SOUTH.turn('R'))
    check("W R N", Heading.NORTH, Heading.WEST.turn('R'))

    check("N L W", Heading.WEST, Heading.NORTH.turn('L'))
    check("W L S", Heading.SOUTH, Heading.WEST.turn('L'))
    check("S L E", Heading.EAST, Heading.SOUTH.turn('L'))
    check("E L N", Heading.NORTH, Heading.EAST.turn('L'))

    check("Part 2: R8, R4, R4, R8", 4, part2("R8, R4, R4, R8"))
}
