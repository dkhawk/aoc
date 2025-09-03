
package com.sphericalchickens.aoc2016gem.day02

import com.sphericalchickens.utils.readInputLines

fun main() {
    println("--- Advent of Code 2016, Day 2 (Gemini) ---")

    val input = readInputLines("aoc2016/day02_input.txt")

    val part1Result = solve(input, Keypad.part1Keypad, '5')
    println("🎁 Part 1: $part1Result")

    val part2Result = solve(input, Keypad.part2Keypad, '5')
    println("🎀 Part 2: $part2Result")
}

private fun solve(input: List<String>, keypad: Keypad, start: Char): String {
    return input.scan(start) { currentButton, instructions ->
        instructions.fold(currentButton) { button, move ->
            keypad.move(button, move)
        }
    }.drop(1).joinToString("")
}

private data class Keypad(private val layout: Map<Char, Map<Char, Char>>) {
    fun move(from: Char, direction: Char): Char {
        return layout[from]?.get(direction) ?: from
    }

    companion object {
        val part1Keypad = Keypad(
            """
             1 2 3
             4 5 6
             7 8 9
            """.trimIndent().toLayout()
        )

        val part2Keypad = Keypad(
            """
                1
              2 3 4
            5 6 7 8 9
              A B C
                D
            """.trimIndent().toLayout()
        )
    }
}

private fun String.toLayout(): Map<Char, Map<Char, Char>> {
    val grid = lines()
    val positions = grid.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, char ->
            if (char.isLetterOrDigit()) char to Position(x, y) else null
        }
    }.toMap()

    return positions.mapValues { (button, pos) ->
        mapOf(
            'U' to (positions.entries.find { it.value == pos.up() }?.key ?: button),
            'D' to (positions.entries.find { it.value == pos.down() }?.key ?: button),
            'L' to (positions.entries.find { it.value == pos.left() }?.key ?: button),
            'R' to (positions.entries.find { it.value == pos.right() }?.key ?: button)
        )
    }
}

private data class Position(val x: Int, val y: Int) {
    fun up() = Position(x, y - 1)
    fun down() = Position(x, y + 1)
    fun left() = Position(x - 1, y)
    fun right() = Position(x + 1, y)
}

//class Day02Test : StringSpec({
//    "part 1 test" {
//        val testInput = """
//            ULL
//            RRDDD
//            LURDL
//            UUUUD
//        """.trimIndent().lines()
//        solve(testInput, Keypad.part1Keypad, '5') shouldBe "1985"
//    }
//
//    "part 2 test" {
//        val testInput = """
//            ULL
//            RRDDD
//            LURDL
//            UUUUD
//        """.trimIndent().lines()
//        solve(testInput, Keypad.part2Keypad, '5') shouldBe "5DB3"
//    }
//})
