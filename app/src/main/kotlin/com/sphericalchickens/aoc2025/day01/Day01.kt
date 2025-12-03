package com.sphericalchickens.aoc2025.day01

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 1 ---")

    val input = readInputLines("aoc2025/day01_input.txt")

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
            part2b(input)
        }
        println("   Part 2: $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}")
    }
}

private fun runPart1Tests() {
    val testInput = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()
    // left (toward lower numbers)
    // right (toward higher numbers)
    // Because the dial is a circle, turning the dial left from 0 one click makes it point at 99. Similarly, turning
    // the dial right from 99 one click makes it point at 0
    // The dial starts by pointing at 50
    // the number of times the dial is left pointing at 0 after any rotation in the sequence
    check("Part 1 Test Case 1", 3, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()
    check("Part 2 Test Case 1", 6, part2b(testInput))

    val input = """
        L68 -> 82, 1
        L30 -> 52, 1
        R48 -> 0, 2
        L5 -> 95, 2
        R60 -> 55, 3
        L55 -> 0, 4
        L1 -> 99, 4
        L99 -> 0, 5
        R14 -> 14, 5
        L82 -> 32, 6
    """.trimIndent().lines()

    var state = State(50)
    input.forEach { line ->
        val action = line.substringBefore("->").trim()
        val (pos, cnt) = line.substringAfter("->").split(",").map { it.trim().toInt() }
        val expectedState = State(pos, cnt)
        check(action, expectedState, countZeroCrossings(state, action))
        state = expectedState
    }
}

/*
The dial starts by pointing at 50.
The dial is rotated L68 to point at 82; during this rotation, it points at 0 once.
The dial is rotated L30 to point at 52.
The dial is rotated R48 to point at 0.
The dial is rotated L5 to point at 95.
The dial is rotated R60 to point at 55; during this rotation, it points at 0 once.
The dial is rotated L55 to point at 0.
The dial is rotated L1 to point at 99.
The dial is rotated L99 to point at 0.
The dial is rotated R14 to point at 14.
The dial is rotated L82 to point at 32; during this rotation, it points at 0 once.
 */

private data class State(
    val position: Int,
    val count: Int = 0
)

private fun part1(input: List<String>): Int {
    return input.fold(State(50)) { state, line ->
        val sign = line[0].let {
            when (it) {
                'L' -> -1
                'R' -> 1
                else -> error("Illegal direction: $it")
            }
        }

        val distance = line.drop(1).toInt()

        val movement = distance * sign


        val p = (state.position + movement).mod(100)
        val c = state.count + if (p == 0) 1 else 0
        State(p,c)
    }.count
}

private fun part2Ugly(input: List<String>): Int {
    return input.fold(State(50)) { state, line ->
        val sign = line[0].let {
            when (it) {
                'L' -> -1
                'R' -> 1
                else -> error("Illegal direction: $it")
            }
        }

        val distance = line.drop(1).toInt()

        var p = state.position
        var c = state.count

        if (sign == -1) {
            repeat(distance) {
                p -= 1
                p = p.mod(100)
                if (p == 0) {
                    c += 1
                }
            }
        }

        if (sign == 1) {
            repeat(distance) {
                p += 1
                p = p.mod(100)
                if (p == 0) {
                    c += 1
                }
            }
        }

        State(p,c)
    }.count
}

private fun part2b(input: List<String>): Int {
    return input.fold(State(50)) { state, line ->
        countZeroCrossings(state, line)
    }.count
}

private fun countZeroCrossings(state: State, action: String) : State {
    val sign = action[0].toSign()
    val distance = action.drop(1).toInt()
    val movement = sign * distance

    var c = abs(movement) / 100
    val p = (state.position + movement).mod(100)

    if (movement < 0 && state.position != 0) {
        if (p > state.position) {
            c += 1
        }
        if (p == 0) {
            c += 1
        }
    }

    if (movement > 0) {
        if (p < state.position) {
            c += 1
        }
    }

    val s = State(p, c)

    return State(s.position, state.count + s.count)
}

private fun Char.toSign(): Int {
    return when (this) {
        'L' -> -1
        'R' -> 1
        else -> error("Illegal direction: $this")
    }
}
