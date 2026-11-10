package com.sphericalchickens.aoc2018.day16

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2018, Day 16: Chronal Classification
 *
 * Placeholder template for Day 16.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2018/day16_input.txt")
    println("\n--- Advent of Code 2018, Day 16: Chronal Classification ---")

    // --- Part 1 ---
    val (part1Result, part1Duration) = measureTimedValue {
        part1(puzzleInput)
    }
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${formatDuration(part1Duration)}")

    // --- Part 2 ---
    val (part2Result, part2Duration) = measureTimedValue {
        part2(puzzleInput)
    }
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${formatDuration(part2Duration)}")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

sealed class Operation

data object Addr : Operation() {
    fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] + regs[b]
        return out
    }
}

data object Addi : Operation() {
    fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] + b
        return out
    }
}

/*
Operations

Addition:

addr (add register) stores into register C the result of adding register A and register B.
addi (add immediate) stores into register C the result of adding register A and value B.
Multiplication:

mulr (multiply register) stores into register C the result of multiplying register A and register B.
muli (multiply immediate) stores into register C the result of multiplying register A and value B.
Bitwise AND:

banr (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
bani (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.
Bitwise OR:

borr (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
bori (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.
Assignment:

setr (set register) copies the contents of register A into register C. (Input B is ignored.)
seti (set immediate) stores value A into register C. (Input B is ignored.)
Greater-than testing:

gtir (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
gtri (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
gtrr (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.
Equality testing:

eqir (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
eqri (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
eqrr (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.

 */

fun part1(input: List<String>): Int {
    // TODO: Implement Part 1
    return 0
}

fun part2(input: List<String>): Int {
    // TODO: Implement Part 2
    return 0
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
    """.trimIndent().lines().filter(String::isNotBlank)

    // TODO: Add real test expectations when implementing
    check("Part 1 stub check", 0, part1(testInput))

    check("Addr", listOf(1, 2, 3, 4) == Addr.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("Addi", listOf(1, 2, 12, 4) == Addi.apply(1, 10, 2, listOf(1, 2, 0, 4)))
}
