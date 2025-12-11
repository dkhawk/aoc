package com.sphericalchickens.aoc2025.day10

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.collections.fold
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
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

    check("Part 1 Test Case 1", 2, testInput[0].toMachine().solve())
    check("Part 1 Test Case 2", 3, testInput[1].toMachine().solve())
    check("Part 1 Test Case 3", 2, testInput[2].toMachine().solve())

    check("Part 1 Test Case 4", 7, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
    """.trimIndent().lines()

    check("Part 2 Test Case 1", 10, testInput[0].toMachine().solveJoltage())
    check("Part 2 Test Case 2", 12, testInput[1].toMachine().solveJoltage())
    check("Part 2 Test Case 3", 11, testInput[2].toMachine().solveJoltage())

    check("Part 2 Test Case 4", 33, part2(testInput))
}

private fun part1(input: List<String>): Int {
    val results = input.toMachines().map { it.solve() ?: error("No solution found for $it") }

    return results.sum()
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

    fun toInt(): Int {
        return if (this == ON) 1 else 0
    }
}

private data class Button(val wiringDiagram: List<Int>) {
    fun toMasks(): Int {
        return wiringDiagram.fold(0) { acc, btn ->
            acc or (1 shl btn)
        }
    }
}

private data class State(
    val lights: Int,
    val presses: Int,
    val error: Int,
    val masks: List<Int>
) {
    fun applyMask(mask: Int, goal: Int): State {
        val newLights = lights xor mask
        return State(
            lights = newLights,
            presses = presses + 1,
            error = newLights xor goal,
            masks = masks - mask
        )
    }
}

private data class JoltageState(
    val joltages: List<Int>,
    val presses: Int,
    val error: Int
) {
//    fun applyMask(button: List<Int>, goal: Int): JoltageState {
//        val newJoltages = joltages
//        return JoltageState(
//            lights = newLights,
//            presses = presses + 1,
//            error = newLights xor goal,
//        )
//    }
}

private data class Machine(val lights: List<Light>, val buttons: List<Button>, val joltages: List<Int>) {
    fun solve() : Int {
        val goal = lights.reversed().toGoal()

        // just to be safe...
        if (goal == 0) return 0

        return solveInternal2(goal, buttons.map { it.toMasks() },)
    }

    private fun solveInternal2(goal: Int, masks: List<Int>): Int {
        val queue = ArrayDeque<State>()

        queue.addFirst(
            State(
                lights = 0,
                presses = 0,
                error = goal.countOneBits(),
                masks = masks
            )
        )

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()

            masks.forEach { mask ->
                val newState = state.applyMask(mask, goal)
                if (newState.error == 0) {
                    return newState.presses
                }

                if (newState.masks.isNotEmpty()) {
                    queue.add(newState)
                }
            }
        }

        error("No solution found for $this")
    }

    fun solveJoltage(): Int {
        return -1
    }
}

private fun List<Light>.toGoal(): Int {
    return fold(0) { acc, ele ->
        (acc shl 1) or ele.toInt()
    }
}

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