package com.sphericalchickens.aoc2016.day08

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = true
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 08 ---")

    val input = readInputLines("aoc2016/day08_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input, 50, 6)
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // Part one already solved part 2.  I was already printing the screen... 🤷

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input)
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
//    check("Part 1 Test Case 1", "expected", part1(testInput))
    check("Part 1 Test Case 1", 6, part1(testInput.take(1), width = 7, height = 3))
    check("Part 1 Test Case 2", 6, part1(testInput.take(2), width = 7, height = 3))
    check("Part 1 Test Case 2", 6, part1(testInput, width = 7, height = 3))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private data class Coordinate(val x: Int, val y: Int)

private class Screen(val width: Int, val height: Int) {
    val pixels = mutableMapOf<Coordinate, Char>()

    override fun toString() : String {
        return buildString {
            for (r in 0 until height) {
                for (c in 0 until width) {
                    append(pixels[Coordinate(c, r)] ?: ' ')
                }
                append("\n")
            }
        }
    }
}

private fun part1(input: List<String>, width: Int, height: Int): Int {
    val screen = Screen(width, height)

    input.forEach { line ->
        line.toCommand().apply(screen)
        screen.println()
    }

    return screen.pixels.count { (_, value) -> value == '#' }
}

private fun String.toCommand() : Command {
    return when {
        this.startsWith("rect") -> {
            val (w, h) = this.substringAfter("rect ").split("x").map { it.toInt() }
            Command.Rect(w, h)
        }
        this.startsWith("rotate column") -> {
            val numbers = Regex("[0-9]+").findAll(this.substringAfter("rotate column "))
                .map(MatchResult::value)
                .map { it.toInt() }
                .toList()
            Command.RotateColumn(numbers[0], numbers[1])
        }
        this.startsWith("rotate row") -> {
            val numbers = Regex("[0-9]+").findAll(this.substringAfter("rotate row "))
                .map(MatchResult::value)
                .map { it.toInt() }
                .toList()
            Command.RotateRow(numbers[0], numbers[1])
        }
        else -> {
            error("Unrecognized command: \"$this\"")
        }
    }
}

private fun part2(input: List<String>): String {
    return ""
}

private sealed interface Command {
    fun apply(screen: Screen)

    data class Rect(val width: Int, val height: Int) : Command {
        override fun apply(screen: Screen) {
            for (c in 0 until width) {
                for (r in 0 until height) {
                    screen.pixels[Coordinate(c, r)] = '#'
                }
            }
        }
    }

    data class RotateColumn(val column: Int, val amount: Int) : Command  {
        override fun apply(screen: Screen) {
            val old = screen.pixels.filter { (key, _) -> key.x == column }

            old.keys.forEach {
                screen.pixels.remove(it)
            }

            old.forEach { (key, value) ->
                val newKey = key.copy(y = (key.y + amount).mod(screen.height))
                screen.pixels[newKey] = value
            }
        }
    }

    data class RotateRow(val row: Int, val amount: Int) : Command  {
        override fun apply(screen: Screen) {
            val old = screen.pixels.filter { (key, _) -> key.y == row }

            old.keys.forEach {
                screen.pixels.remove(it)
            }

            old.forEach { (key, value) ->
                val newKey = key.copy(x = (key.x + amount).mod(screen.width))
                screen.pixels[newKey] = value
            }
        }
    }
}
