package com.sphericalchickens.aoc2016.day02

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines

fun main() {
    println("--- Advent of Code 2016, Day 2 ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day02_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")
}

fun part1(input: List<String>): String {
    return input.fold('5' to "") { acc, instructions ->
        val button = instructions.fold(acc.first) { acc, ch ->
            movementMap[acc]!![ch]!!
        }
        button to (acc.second + button)
    }.second
}

fun part2(input: List<String>): String {
    return input.fold('5' to "") { acc, instructions ->
        val button = instructions.fold(acc.first) { acc, ch ->
            movementMap2[acc]!![ch]!!
        }
        button to (acc.second + button)
    }.second
}

val movementMap =
"""1 2 3
4 5 6
7 8 9""".filterNot { it == ' ' }.toMovementMap()

val movementMap2 =
""" 1
  2 3 4
5 6 7 8 9
  A B C
    D""".centered().toMovementMap()

private fun String.centered(): String {
    val l = lines().map { it.filterNot { it.isWhitespace() } }
    val w = l.maxBy { it.length }.length

    val o = l.joinToString("\n") { it ->
        val padding = (w - it.length) / 2
        ' '.toString().repeat(padding) + it + ' '.toString().repeat(padding)
    }

    return o
}

private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }
}

private enum class Heading(val symbol: Char, val vector: Vector) {
    Up('U', Vector(0, -1)),
    Down('D', Vector(0, 1)),
    Left('L', Vector(-1, 0)),
    Right('R', Vector(1, 0))
}

private fun String.toMovementMap(): Map<Char, Map<Char, Char>> {
//    val width = sqrt(this.length.toFloat()).toInt()
    val width = lines().first().length

    // Strip whitespace
    // Map to coordinates
    val m1 = this.filterNot { it == '\n' }.withIndex().mapNotNull { (index, ch) ->
        if (ch.isLetterOrDigit()) {
            val x = index % width
            val y = index / width
            Vector(x, y) to ch
        } else {
            null
        }
    }.toMap()

    return m1.entries.associate { (vec, buttonValue) ->
        buttonValue to Heading.entries.associate { heading ->
            val neighbor = vec + heading.vector
            heading.symbol to (m1[neighbor] ?: m1[vec]!!)
        }
    }
}

private fun runTests() {
    // Add tests here
    /*
        1 2 3
        4 5 6
        7 8 9
        Suppose your instructions are:

        ULL
        RRDDD
        LURDL
        UUUUD
        You start at "5" and move up (to "2"), left (to "1"), and left (you can't, and stay on "1"), so the first button is 1.
        Starting from the previous button ("1"), you move right twice (to "3") and then down three times (stopping at "9" after two moves and ignoring the third), ending up with 9.
        Continuing from "9", you move left, up, right, down, and left, ending with 8.
        Finally, you move up four times (stopping at "2"), then down once, ending with 5.
        So, in this example, the bathroom code is 1985.
     */

    val testInput = """
        ULL to 1
        RRDDD to 9
        LURDL to 8
        UUUUD to 5""".trimIndent().lines().map { line ->
            val (a ,  b) = line.split(" to ")
            a to b.first()
        }

    val code1 = testInput.fold('5' to "") { acc, pair ->
        val (instructions, expected ) = pair

        val button = instructions.fold(acc.first) { acc, ch ->
            movementMap[acc]!![ch]!!
        }

        check("$pair", expected, button,)

        button to (acc.second + button)
    }.second

    check("code 1", "1985", code1)

    val testInput2 = """
        ULL
        RRDDD
        LURDL
        UUUUD""".lines().filterNot(String::isEmpty).map { it.trim() }

    check("part 2",
        "5DB3",
        part2(testInput2)
    )
}
