package com.sphericalchickens.aoc2016gem.day08

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import java.util.Collections
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false // Original had no Part 2 tests
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 08 ---")

    val input = readInputLines("aoc2016/day08_input.txt")
    val puzzleWidth = 50
    val puzzleHeight = 6

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            // Pass in the puzzle dimensions
            val part1Result = part1(input, puzzleWidth, puzzleHeight)
            println("   Part 1: $part1Result") // Original answer: 123
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            // Part 2 also needs the dimensions and prints the screen
            val part2Result = part2(input, puzzleWidth, puzzleHeight)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        rect 3x2
        rotate column x=1 by 1
        rotate row y=0 by 4
        rotate column x=1 by 1
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 6, part1(testInput.take(1), width = 7, height = 3))
    check("Part 1 Test Case 2", 6, part1(testInput.take(2), width = 7, height = 3))
    check("Part 1 Test Case 3", 6, part1(testInput, width = 7, height = 3))
}

private fun runPart2Tests() {
    // Original had no tests
}

// This data class is perfect as-is.
private data class Coordinate(val x: Int, val y: Int)

/**
 * A dense 2D representation of the screen.
 *
 * We use a List<MutableList<Boolean>>.
 * The outer list represents the rows (y-axis) and is immutable.
 * Each inner list is a row (x-axis) and is mutable to allow for rotations.
 */
private class Screen(val width: Int, val height: Int) {
    // Initialize a dense 2D grid of false (off) pixels.
    // pixels[y][x]
    val pixels: List<MutableList<Boolean>> = List(height) {
        MutableList(width) { false }
    }

    /**
     * Generates a string representation of the screen.
     */
    override fun toString(): String {
        return pixels.joinToString("\n") { row ->
            row.joinToString("") { if (it) "#" else " " }
        }
    }

    /**
     * Counts the total number of lit pixels.
     */
    fun countLitPixels(): Int {
        // sumOf is a great stdlib function for this.
        return pixels.sumOf { row -> row.count { it } }
    }
}

/**
 * Creates a screen and applies all commands from the input.
 */
private fun buildScreen(input: List<String>, width: Int, height: Int): Screen {
    val screen = Screen(width, height)
    // We can use forEach on the input list directly.
    input.forEach { line ->
        // The `apply` scope function is perfect here: it applies the
        // command to the screen and returns Unit.
        line.toCommand().apply(screen)
    }
    return screen
}

private fun part1(input: List<String>, width: Int, height: Int): Int {
    val screen = buildScreen(input, width, height)
    return screen.countLitPixels()
}

private fun part2(input: List<String>, width: Int, height: Int): String {
    val screen = buildScreen(input, width, height)
    // Add a newline for clean printing in the console.
    return "\n" + screen.toString()
}

// We define the regex patterns as constants.
private val RECT_REGEX = """rect (\d+)x(\d+)""".toRegex()
private val ROTATE_COL_REGEX = """rotate column x=(\d+) by (\d+)""".toRegex()
private val ROTATE_ROW_REGEX = """rotate row y=(\d+) by (\d+)""".toRegex()

private fun String.toCommand(): Command {
    // We use a `when` expression, which is idiomatic Kotlin.
    return when {
        // Using `matchEntire` with our regex is safer than `startsWith`.
        // The `!!` is safe because we know the regex will match.
        RECT_REGEX.matches(this) -> {
            // We can destructure the captured groups (index 0 is the full match).
            val (_, w, h) = RECT_REGEX.matchEntire(this)!!.groupValues
            Command.Rect(w.toInt(), h.toInt())
        }

        ROTATE_COL_REGEX.matches(this) -> {
            val (_, col, amount) = ROTATE_COL_REGEX.matchEntire(this)!!.groupValues
            Command.RotateColumn(col.toInt(), amount.toInt())
        }

        ROTATE_ROW_REGEX.matches(this) -> {
            val (_, row, amount) = ROTATE_ROW_REGEX.matchEntire(this)!!.groupValues
            Command.RotateRow(row.toInt(), amount.toInt())
        }
        // Always good to have an error case for unrecognized input.
        else -> error("Unrecognized command: \"$this\"")
    }
}

// The sealed interface is unchanged, it's a great pattern.
private sealed interface Command {
    fun apply(screen: Screen)

    data class Rect(val width: Int, val height: Int) : Command {
        override fun apply(screen: Screen) {
            // Simple nested loop to set values in our 2D list.
            for (y in 0 until height) {
                for (x in 0 until width) {
                    screen.pixels[y][x] = true
                }
            }
        }
    }

    data class RotateColumn(val column: Int, val amount: Int) : Command {
        override fun apply(screen: Screen) {
            // 1. Create a snapshot of the current column's values.
            // We use List(size) { ... } to build a new immutable list.
            val currentColumn = List(screen.height) { y ->
                screen.pixels[y][column]
            }

            // 2. Write the pixels back into their new positions.
            for (y in 0 until screen.height) {
                // The pixel *now* at row `y` came from `(y - amount)`
                // The .mod() operator handles the wrap-around perfectly.
                val oldY = (y - amount).mod(screen.height)
                screen.pixels[y][column] = currentColumn[oldY]
            }
        }
    }

    data class RotateRow(val row: Int, val amount: Int) : Command {
        override fun apply(screen: Screen) {
            // This is the most elegant change.
            // screen.pixels[row] is the MutableList<Boolean> for that row.
            // We can rotate it in-place using the Java stdlib.
            Collections.rotate(screen.pixels[row], amount)
        }
    }
}

