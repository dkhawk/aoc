package com.sphericalchickens.aoc2015.day06

import com.sphericalchickens.utils.readInputLines
import kotlin.math.max

/**
 * # Advent of Code 2015, Day XX: TBD
 *
 * This program solves the puzzle for Day XX.
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day06_input.txt")
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

private enum class State {
    ON,
    OFF
}

private data class Vector(
    val x: Int,
    val y: Int
) : Comparable<Vector> {
    override fun compareTo(other: Vector): Int {
        return compareBy(Vector::y, Vector::x).compare(this, other)
    }

    operator fun rangeTo(that: Vector): VectorRange {
        return VectorRange(start = this@Vector, endInclusive = that)
    }
}

private class VectorRange(
    override val start: Vector,
    override val endInclusive: Vector
) : ClosedRange<Vector>, Iterable<Vector> {

    override fun iterator(): Iterator<Vector> {
        return object : Iterator<Vector> {
            var current = start

            override fun hasNext(): Boolean {
                return current <= endInclusive
            }

            fun nextVector(): Vector {
                var nextX = current.x + 1
                var nextY = current.y

                if (nextX > endInclusive.x) {
                    nextX = start.x
                    nextY += 1
                }

                return Vector(nextX, nextY)
            }

            override fun next(): Vector {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val result = current

                current = nextVector()

                return result
            }
        }
    }
}

private sealed class Command {
    abstract val start: Vector
    abstract val end: Vector

    operator fun invoke(onLights: MutableSet<Vector>) {
        for (item in start..end) {
            doIt(onLights, item)
        }
    }

    protected abstract fun doIt(onLights: MutableSet<Vector>, v: Vector)

    data class TurnOn(override val start: Vector, override val end: Vector) : Command() {
        override fun doIt(
            onLights: MutableSet<Vector>,
            v: Vector
        ) {
            onLights.add(v)
        }

    }

    data class TurnOff(override val start: Vector, override val end: Vector) : Command() {
        override fun doIt(
            onLights: MutableSet<Vector>,
            v: Vector
        ) {
            onLights.remove(v)
        }
    }

    data class Toggle(override val start: Vector, override val end: Vector) : Command() {
        override fun doIt(
            onLights: MutableSet<Vector>,
            v: Vector
        ) {
            if (onLights.contains(v)) {
                onLights.remove(v)
            } else {
                onLights.add(v)
            }
        }
    }
}

private sealed class Command2 {
    abstract val start: Vector
    abstract val end: Vector

    operator fun invoke(onLights: MutableMap<Vector, Int>) {
        for (item in start..end) {
            doIt(onLights, item)
        }
    }

    protected abstract fun doIt(onLights: MutableMap<Vector, Int>, v: Vector)

    data class TurnOn(override val start: Vector, override val end: Vector) : Command2() {
        override fun doIt(
            onLights: MutableMap<Vector, Int>,
            v: Vector
        ) {
            val old = onLights.getOrElse(v) { 0 }
            onLights[v] = old + 1
        }

    }

    data class TurnOff(override val start: Vector, override val end: Vector) : Command2() {
        override fun doIt(
            onLights: MutableMap<Vector, Int>,
            v: Vector
        ) {
            val old = onLights.getOrElse(v) { 0 }
            onLights[v] = max(old - 1, 0)
        }
    }

    data class Toggle(override val start: Vector, override val end: Vector) : Command2() {
        override fun doIt(
            onLights: MutableMap<Vector, Int>,
            v: Vector
        ) {
            val old = onLights.getOrElse(v) { 0 }
            onLights[v] = old + 2
        }
    }
}

fun part1(input: List<String>): Int {
    // turn on 489,959 through 759,964
    // turn off 820,516 through 871,914
    // toggle 749,672 through 973,965

    val onLights = mutableSetOf<Vector>()

    input.filterNot { it.isBlank() }.map { it.toCommand() }.forEach { command ->
        command(onLights)
    }

    return onLights.size
}

private fun String.toCommand(): Command {
    return when {
        contains("toggle") -> this.toToggle()
        contains("on") -> {
            val (_, _, start, _, end) = split(" ")
            Command.TurnOn(start.toVector(), end.toVector())
        }
        contains("off") -> {
            val (_, _, start, _, end) = split(" ")
            Command.TurnOff(start.toVector(), end.toVector())
        }
        else -> error("Unknown command: $this")
    }
}

private fun String.toCommand2(): Command2 {
    return when {
        contains("toggle") -> {
            val (_,  start, _, end) = split(" ")
            Command2.Toggle(start.toVector(), end.toVector())
        }
        contains("on") -> {
            val (_, _, start, _, end) = split(" ")
            Command2.TurnOn(start.toVector(), end.toVector())
        }
        contains("off") -> {
            val (_, _, start, _, end) = split(" ")
            Command2.TurnOff(start.toVector(), end.toVector())
        }
        else -> error("Unknown command: $this")
    }
}

private fun String.toToggle(): Command.Toggle {
    val (_,  start, _, end) = split(" ")
    return Command.Toggle(start.toVector(), end.toVector())
}

private fun String.toVector(): Vector {
    val (x, y) = split(",").map { it.toInt() }
    return Vector(x, y)
}

fun part2(input: List<String>): Int {
    val onLights = mutableMapOf<Vector, Int>()

    input.filterNot { it.isBlank() }.map { it.toCommand2() }.forEach { command ->
        command(onLights)
    }

    return onLights.values.sum()
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    val testCommands = """
        turn on 489,959 through 759,964
        turn off 820,516 through 871,914
        toggle 749,672 through 973,965
    """.trimIndent().lines()

    check(testCommands[0].toCommand() == Command.TurnOn( Vector(489,959), Vector(759,964)))
    check(testCommands[1].toCommand() == Command.TurnOff( Vector(820,516), Vector(871,914)))
    check(testCommands[2].toCommand() == Command.Toggle( Vector(749,672), Vector(973,965)))

    val onLights = mutableSetOf<Vector>()
    check(onLights.isEmpty())

    (Vector(0, 0)..Vector(5, 5)).forEach { println(it) }

    "turn on 0,0 through 999,999".toCommand()(onLights)
    check(onLights.size == 1000 * 1000)

    "toggle 0,0 through 999,0".toCommand()(onLights)
    check(onLights.size == 1000 * (1000 - 1))

//    check(part1(testInput1) == 0)

    // Part 2 Test Cases
//    val testInput2 = """
//    """.trimIndent().lines()
//    check(part2(testInput2) == 0)
}
