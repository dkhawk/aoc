package com.sphericalchickens.aoc2025.day10

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 10 ---")

    val input = readInputLines("aoc2025/day10_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
            part1(input)
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

private fun runPart1Tests() {
    val testInput = """
        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
    """.trimIndent().lines()



    check("Part 1 Test Case 1", 7, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private fun part1(input: List<String>): Int {
    val machines = input.toMachines()

    return -1
}

private fun List<String>.toMachines(): List<Machine> {
    return map { it.toMachine() }
}

private fun part2(input: List<String>): Int {
    return -1
}

private enum class Light(val ch: Char) {
    ON('#'), OFF('.');

    override fun toString(): String {
        return ch.toString()
    }
}

private data class Button(val wiringDiagram: List<Int>)

private data class Machine(val lights: List<Light>, val buttons: List<Button>, val joltages: List<Int>)

private fun String.toMachine(): Machine {
    val lightsString = substringBefore(" ")
    val joltagesString = substringAfterLast(" ")
    val buttonString = substring(lightsString.length + 1, length - (joltagesString.length + 1))

    val lights = lightsString.mapNotNull {
        when (it) {
            '.' -> Light.OFF
            '#' -> Light.ON
            else -> null
        }
    }

    val buttons = buttonString.split(" ").map {
        Button(it.drop(1).dropLast(1).split(",").map { it.toInt() })
    }

    val joltages = joltagesString.drop(1).dropLast(1).split(",").map { it.toInt() }

    return Machine(
        lights = lights,
        buttons = buttons,
        joltages = joltages
    )
}