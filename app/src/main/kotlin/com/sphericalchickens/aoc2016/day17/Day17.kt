package com.sphericalchickens.aoc2016.day17

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import java.security.MessageDigest
import java.util.ArrayDeque
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 17 ---")

    val input = readInputLines("aoc2016/day17_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input)
            println("   Part 1: $part1Result")
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
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 1 Test Case 1", listOf(Heading.UP, Heading.DOWN, Heading.LEFT), "ced9".openDoors())

    check("1 TC 1.b", listOf(Vector(0, 1)), "ced9".openDoors().validNextRooms(Vector(0, 0)).map { it.second })

    check("Part 1 Test Case 2", listOf(Heading.UP, Heading.LEFT, Heading.RIGHT), "f2bc".openDoors())
    check("1 TC 2.b", listOf(Vector(0, 0), Vector(1, 1)), "f2bc".openDoors().validNextRooms(Vector(0, 1)).map { it.second })

    check("1 TC 3", "DDRRRD", part1(listOf("ihgpwlah")))
    check("1 TC 4", "DDUDRLRRUDRD", part1(listOf("kglvqrro")))
    check("1 TC 5", "DRURDRUDDLLDLUURRDULRLDUUDDDRR", part1(listOf("ulqzkmiv")))
}

private const val MAX_COORD = 3
private const val MIN_COORD = 0
private val VALID_RANGE = MIN_COORD .. MAX_COORD

private fun List<Heading>.validNextRooms(vector: Vector): List<Pair<Heading, Vector>> {
    return this.map { heading ->
        heading to vector.move(heading)
    }.filter {(heading, location) ->
        location.isValidLocation()
    }
}

private fun runPart2Tests() {
    check("Part 2 Test Case 1", 370, part2(listOf("ihgpwlah")))
    check("Part 2 Test Case 2", 492, part2(listOf("kglvqrro")))
    check("Part 2 Test Case 3", 830, part2(listOf("ulqzkmiv")))
}

private data class Vector(
    val x: Int,
    val y: Int
)

private fun Vector.isValidLocation() = (this.x in VALID_RANGE) && (this.y in VALID_RANGE)

private operator fun Vector.plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)

private enum class Heading(val vector: Vector, val symbol: Char) {
    UP(Vector(0, -1), 'U'),
    DOWN(Vector(0, 1), 'D'),
    LEFT(Vector(-1, 0), 'L'),
    RIGHT(Vector(1, 0), 'R')
}

private fun Vector.move(heading: Heading) = this + heading.vector

private fun String.openDoors(): List<Heading> {
    return this.take(4)
        .map { it in 'b'..'f' }
        .zip(Heading.entries.toTypedArray())
        .filter { it.first }.map { it.second }
}

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.fold("") { str, byte -> str + "%02x".format(byte) }
}

private val GOAL = Vector(MAX_COORD, MAX_COORD)

private data class State(
    val location: Vector,
    val path: String
)

private fun part1(input: List<String>): String {
    val password = input.first()

    val initialState = State(Vector(0, 0), "")
    val nextPossibleStates = ArrayDeque(listOf(initialState))

    // Assume there is an answer!!
    while (true) {
        val (location, path) = nextPossibleStates.poll()

        val hash = "$password$path".md5()

        val next = hash.openDoors()
            .validNextRooms(location)
            .map { (heading, location) ->
                val newPath = path + heading.symbol
                if (location == GOAL) {
                    return newPath
                }
                State(location, newPath)
            }

        nextPossibleStates.addAll(next)
    }
}

private fun part2(input: List<String>): Int {
    val password = input.first()

    val initialState = State(Vector(0, 0), "")
    val nextPossibleStates = ArrayDeque(listOf(initialState))

    var longest = 0

    while (!nextPossibleStates.isEmpty()) {
        val (location, path) = nextPossibleStates.poll()

        val hash = "$password$path".md5()

        val next = hash.openDoors()
            .validNextRooms(location)
            .mapNotNull { (heading, location) ->
                val newPath = path + heading.symbol
                if (location == GOAL) {
                    if (newPath.length > longest) {
                        longest = newPath.length
                    }
                    null
                } else {
                    State(location, newPath)
                }
            }

        nextPossibleStates.addAll(next)
    }

    return longest
}
