package com.sphericalchickens.app.aoc2015gem.day06

import com.sphericalchickens.utils.readInputLines
import kotlin.math.max

/**
 * # Advent of Code 2015, Day 6: Probably a Fire Hazard
 *
 * This program simulates turning lights on and off in a 1000x1000 grid.
 * The goal is to determine the number of lit lights (Part 1) and the
 * total brightness (Part 2) after executing a series of instructions.
 *
 * ## Literate Programming Approach
 *
 * This file is structured to be read like a document. The code and its
 * explanation are woven together to make the logic clear and easy to follow.
 * We'll start with the main entry point and then explore the data structures
 * and logic used to solve the puzzle.
 */
fun main() {
    val puzzleInput = readInputLines("aoc2015/day06_input.txt")

    println("--- Advent of Code 2015, Day 6 ---")

    val part1Result = solvePart1(puzzleInput)
    println("🎁 Part 1: There are $part1Result lights on.")

    val part2Result = solvePart2(puzzleInput)
    println("🎀 Part 2: The total brightness is $part2Result.")
}

// ============================================================================================
// Data Representation
// ============================================================================================

/**
 * Represents a single coordinate in the light grid.
 * We use a `data class` for automatic `equals()`, `hashCode()`, and `toString()` generation,
 * which is perfect for use as keys in a Map or elements in a Set.
 */
private data class Coordinate(val x: Int, val y: Int)

/**
 * Represents the three types of actions that can be performed on the lights.
 * An `enum class` is ideal here as it provides a type-safe way to represent a fixed set of constants.
 */
private enum class Action {
    TURN_ON, TURN_OFF, TOGGLE
}

/**
 * A parsed instruction, containing the action to perform and the rectangular
 * region of lights it applies to, defined by a start and end coordinate.
 */
private data class Instruction(val action: Action, val start: Coordinate, val end: Coordinate)

// ============================================================================================
// Parsing Logic
// ============================================================================================

/**
 * A regular expression to parse the instruction strings. Using a regex is more robust
 * and concise than splitting the string. It captures the essential parts of the command.
 *
 * - `(turn on|turn off|toggle)`: Captures the action.
 * - `(\d+),(\d+)`: Captures the x and y of the start coordinate.
 * - `(\d+),(\d+)`: Captures the x and y of the end coordinate.
 */
private val instructionRegex = """(turn on|turn off|toggle) (\d+),(\d+) through (\d+),(\d+)""".toRegex()

/**
 * An extension function on `String` to parse it into an `Instruction`.
 * This keeps the parsing logic cleanly separated and associated with the type it operates on.
 */
private fun String.toInstruction(): Instruction {
    val match = instructionRegex.find(this) ?: error("Invalid instruction format: $this")
    val (actionStr, x1, y1, x2, y2) = match.destructured

    val action = when (actionStr) {
        "turn on" -> Action.TURN_ON
        "turn off" -> Action.TURN_OFF
        "toggle" -> Action.TOGGLE
        else -> error("Unknown action: $actionStr")
    }

    return Instruction(
        action = action,
        start = Coordinate(x1.toInt(), y1.toInt()),
        end = Coordinate(x2.toInt(), y2.toInt())
    )
}

// ============================================================================================
// Solution Logic
// ============================================================================================

/**
 * A generic grid processing function. It iterates through each instruction and applies a
 * given update function to every coordinate within the instruction's bounds.
 *
 * This higher-order function abstracts the grid iteration and state management,
 * allowing `solvePart1` and `solvePart2` to provide only the specific logic for
 * updating a light's state.
 *
 * @param T The type of the value stored for each light (e.g., Boolean for on/off, Int for brightness).
 * @param instructions The list of raw string instructions.
 * @param initialGrid The starting state of the grid.
 * @param updateFn A function that takes the current state of a light and an action, and returns the new state.
 * @return The final state of the grid after all instructions are executed.
 */
private fun <T> processGrid(
    instructions: List<String>,
    initialGrid: Map<Coordinate, T>,
    updateFn: (currentValue: T?, action: Action) -> T?
): Map<Coordinate, T> {
    val grid = initialGrid.toMutableMap()
    val parsedInstructions = instructions.filter { it.isNotBlank() }.map { it.toInstruction() }

    for (instruction in parsedInstructions) {
        for (x in instruction.start.x..instruction.end.x) {
            for (y in instruction.start.y..instruction.end.y) {
                val coordinate = Coordinate(x, y)
                val newValue = updateFn(grid[coordinate], instruction.action)
                if (newValue != null) {
                    grid[coordinate] = newValue
                } else {
                    grid.remove(coordinate)
                }
            }
        }
    }
    return grid
}

/**
 * Solves Part 1 of the puzzle.
 *
 * The grid stores `Boolean` values, where `true` means a light is on.
 * We use a `Map<Coordinate, Boolean>` to represent the grid. This is a "sparse" representation,
 * meaning we only store entries for lights that are on. This can be more memory-efficient
 * if fewer than half the lights are expected to be on.
 */
fun solvePart1(input: List<String>): Int {
    val finalGrid = processGrid(input, emptyMap<Coordinate, Boolean>()) { isLit, action ->
        val currentlyOn = isLit ?: false
        when (action) {
            Action.TURN_ON -> true
            Action.TURN_OFF -> null // Remove from map to signify 'off'
            Action.TOGGLE -> if (currentlyOn) null else true
        }
    }
    return finalGrid.size
}

/**
 * Solves Part 2 of the puzzle.
 *
 * The grid stores `Int` values representing the brightness of each light.
 * The default brightness is 0. The map only stores lights with brightness > 0.
 */
fun solvePart2(input: List<String>): Int {
    val finalGrid = processGrid(input, emptyMap<Coordinate, Int>()) { brightness, action ->
        val currentBrightness = brightness ?: 0
        val newBrightness = when (action) {
            Action.TURN_ON -> currentBrightness + 1
            Action.TURN_OFF -> max(0, currentBrightness - 1)
            Action.TOGGLE -> currentBrightness + 2
        }
        if (newBrightness > 0) newBrightness else null // Remove if brightness is 0
    }
    return finalGrid.values.sum()
}
