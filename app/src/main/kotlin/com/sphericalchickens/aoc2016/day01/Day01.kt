package com.sphericalchickens.aoc2016.day01

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs
import kotlin.text.split

fun main() {
    println("--- Advent of Code 2016, Day 1 ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day01_input.txt")

    val part1Result = part1(input.first())
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input.first())
    println("🎀 Part 2: $part2Result")
}

private val origin = Position(Vector(0, 0), Heading.NORTH)

fun part1(input: String): Int {
    return inputSequence(input).fold(origin) { acc, instruction ->
        instruction(acc)
    }.distanceTo(origin)
}

private fun inputSequence(input: String): Sequence<Instruction> = sequence {
    input.split(", ").forEach { inst ->
        val direction = inst.first()
        val distance = inst.drop(1).toInt()
        yield(Instruction(direction, distance))
    }
}

private data class Instruction(
    val direction: Char,
    val distance: Int
) {
    operator fun invoke(position: Position): Position {
        return position.turn(direction).advance(distance)
    }
}

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
}

private enum class Heading(val vector: Vector) {
    NORTH(Vector(0, -1)),
    EAST(Vector(1, 0)),
    SOUTH(Vector(0, 1)),
    WEST(Vector(-1, 0));

    fun turn(direction: Char): Heading {
        return when (direction) {
            'R' -> Heading.entries[(this.ordinal + 1) % Heading.entries.size]
            'L' -> Heading.entries[(this.ordinal + (Heading.entries.size - 1)) % Heading.entries.size]
            else -> error("Invalid direction: $direction")
        }
    }
}

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
        return abs(location.x - loc.location.x) + abs(location.y - loc.location.y)
    }
}

fun part2(input: String): Int {
    var location = origin
    val visited = mutableSetOf(origin.location)

    var bunnyHq = location

     val s = inputSequence(input)
         .first { instruction ->
             var next = location.turn(instruction.direction)

             var remaining = instruction.distance

             var done = false

             while (!done && remaining > 0) {
                 next = next.advance(1)
                 remaining--

                 done = visited.contains(next.location).also {
                     visited.add(next.location)
                 }
             }

             location = next

             if (done) {
                 bunnyHq = next
             }

             done
         }

    return bunnyHq.distanceTo(origin)
}

private fun runTests() {
    // Add tests here
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
        check("$testCase", testCase.second, part1(testCase.first))
    }

    check("N R E", Heading.EAST, Heading.NORTH.turn('R'))
    check("E R S", Heading.SOUTH, Heading.EAST.turn('R'))
    check("S R W", Heading.WEST, Heading.SOUTH.turn('R'))
    check("W R N", Heading.NORTH, Heading.WEST.turn('R'))

    check("N L W", Heading.WEST, Heading.NORTH.turn('L'))
    check("W L S", Heading.SOUTH, Heading.WEST.turn('L'))
    check("S L E", Heading.EAST, Heading.SOUTH.turn('L'))
    check("E L N", Heading.NORTH, Heading.EAST.turn('L'))

    check("R8, R4, R4, R8", 4, part2("R8, R4, R4, R8"))
}
