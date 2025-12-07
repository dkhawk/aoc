package com.sphericalchickens.aoc2025.day06

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 6 ---")

    val input = readInputLines("aoc2025/day06_input.txt")

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
        123 328  51 64 
         45 64  387 23 
          6 98  215 314
        *   +   *   +  
    """.trimIndent().lines().filterNot { it.isBlank() }

    check("Part 1 Test Case 1", 4277556L, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        123 328  51 64 
         45 64  387 23 
          6 98  215 314
        *   +   *   +  
    """.trimIndent().lines().filterNot { it.isBlank() }
    check("Part 2 Test Case 1", 3263827L, part2(testInput))
}

private val re = Regex("""\s+""")
private fun part1(input: List<String>): Long {
    val transposed = input.map { line -> line.trim().split(re) }.transpose()
    return transposed.sumOf { problem ->
        val operation = problem.last()
        val seed = if (operation == "+") 0L else 1L
        problem.dropLast(1).fold(seed) { acc, ele ->
            val n = ele.toLong()
            when (operation) {
                "+" -> acc + n
                "*" -> acc * n
                else -> error("unexpected operation: $operation")
            }
        }
    }
}

private fun List<List<String>>.transpose(): List<List<String>> {
    val input = this
    check(this.all { it.size == this.first().size })
    return buildList {
        for (i in input.first().indices) {
            add(input.map { it[i] })
        }
    }
}

private sealed interface Operation {
    fun applyToNumbers(numbers: List<Long>) : Long
}

private data object Addition : Operation {
    override fun applyToNumbers(numbers: List<Long>): Long {
        return numbers.sum()
    }
}

private data object Multiplication : Operation {
    override fun applyToNumbers(numbers: List<Long>): Long {
        return numbers.fold(1L) { acc, ele ->
            acc * ele
        }
    }
}

private fun part2(input: List<String>): Long {
    val operatorLine = input.last()

    val startColumns = operatorLine.withIndex().filterNot { it.value == ' ' }.map { it.index }.plusElement(operatorLine.length + 1)

    val deltas = startColumns.zipWithNext { a, b -> (b - a) - 1 }

    val gridOfNumbers = input.dropLast(1)

    val columns = startColumns.zip(deltas).map { (a, b) ->
        gridOfNumbers.map { line -> line.substring(a, a + b) }
    }

    val numbers = columns.map { column ->
        val len = column.first().lastIndex
        len.downTo(0).map { index ->
            column.map { it[index] }.joinToString("").trim().toLong()
        }
    }

    val operators = operatorLine.mapNotNull {
        when (it) {
            '+' -> Addition
            '*' -> Multiplication
            else -> null
        }
    }

    return numbers.zip(operators).sumOf { (numbers, operator) -> operator.applyToNumbers(numbers) }
}
