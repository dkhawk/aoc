package com.sphericalchickens.aoc2015gem.day07

import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 07: Some Assembly Required
 *
 * This program simulates a digital circuit composed of logic gates and wires.
 * The circuit is defined by a series of instructions that describe how signals
 * are manipulated and passed between wires. The goal is to determine the signal
 * on a specific wire, 'a'.
 *
 * The circuit supports the following operations:
 * - **AND**: Bitwise AND of two signals.
 * - **OR**: Bitwise OR of two signals.
 * - **LSHIFT**: Bitwise left shift.
 * - **RSHIFT**: Bitwise right shift.
 * - **NOT**: Bitwise complement.
 * - **COPY**: A direct signal assignment.
 *
 * All signals are 16-bit unsigned integers (0-65535).
 *
 * ## Part 1
 *
 * Determine the signal provided to wire 'a'.
 *
 * ## Part 2
 *
 * Take the signal from wire 'a' in Part 1, override the signal on wire 'b' with
 * this value, and then determine the new signal on wire 'a'.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day07_input.txt")
    println("\n--- Advent of Code 2015, Day 07 ---")


    // --- Part 1 ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")


    // --- Part 2 ---
    val part2Result = part2(puzzleInput, part1Result)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * A sealed class representing a single operation in the circuit.
 * Each instruction targets a specific output wire (register).
 */
private sealed class Instruction {
    abstract val register: String

    data class And(val in1: String, val in2: String, override val register: String) : Instruction()
    data class Or(val in1: String, val in2: String, override val register: String) : Instruction()
    data class Lshift(val in1: String, val in2: String, override val register: String) : Instruction()
    data class Rshift(val in1: String, val in2: String, override val register: String) : Instruction()
    data class Not(val in1: String, override val register: String) : Instruction()
    data class Copy(val in1: String, override val register: String) : Instruction()
}

/**
 * Represents the entire circuit as a map of wire names to instructions.
 */
private typealias Circuit = Map<String, Instruction>

/**
 * Solves for the signal on wire 'a'.
 */
fun part1(input: List<String>): Int {
    val circuit = input.mapNotNull { it.toInstruction() }.associateBy { it.register }
    return circuit.resolve("a")
}

/**
 * Solves for the signal on wire 'a' after overriding wire 'b'.
 */
fun part2(input: List<String>, bValue: Int): Int {
    val circuit = input.mapNotNull { it.toInstruction() }.associateBy { it.register }
    val memo = mutableMapOf("b" to bValue)
    return circuit.resolve("a", memo)
}

/**
 * Recursively resolves the signal for a given wire using memoization to cache results.
 *
 * @param wire The name of the wire to resolve.
 * @param memo A cache of previously computed wire values.
 * @return The 16-bit signal value on the wire.
 */
private fun Circuit.resolve(wire: String, memo: MutableMap<String, Int> = mutableMapOf()): Int {
    return run {
        memo[wire]
            ?: wire.toIntOrNull()
            ?: (when (val instruction = this.getValue(wire)) {
                is Instruction.And -> resolve(instruction.in1, memo) and resolve(instruction.in2, memo)
                is Instruction.Or -> resolve(instruction.in1, memo) or resolve(instruction.in2, memo)
                is Instruction.Lshift -> resolve(instruction.in1, memo) shl resolve(instruction.in2, memo)
                is Instruction.Rshift -> resolve(instruction.in1, memo) shr resolve(instruction.in2, memo)
                is Instruction.Not -> resolve(instruction.in1, memo).inv()
                is Instruction.Copy -> resolve(instruction.in1, memo)
            } and 0xFFFF) // Ensure the result is a 16-bit value.
    }.also {
        // Cache the result.
        memo[wire] = it
    }
}

// ---------------------------------------------------------------------------------------------
// Parsing & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * A set of regular expressions to parse the different instruction formats.
 */
private val instructionRegexes = mapOf(
    Regex("""^(\w+) AND (\w+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, in2, reg) = m.destructured
        Instruction.And(in1, in2, reg)
    },
    Regex("""^(\w+) OR (\w+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, in2, reg) = m.destructured
        Instruction.Or(in1, in2, reg)
    },
    Regex("""^(\w+) LSHIFT (\d+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, in2, reg) = m.destructured
        Instruction.Lshift(in1, in2, reg)
    },
    Regex("""^(\w+) RSHIFT (\d+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, in2, reg) = m.destructured
        Instruction.Rshift(in1, in2, reg)
    },
    Regex("""^NOT (\w+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, reg) = m.destructured
        Instruction.Not(in1, reg)
    },
    Regex("""^(\w+) -> (\w+)$""") to { m: MatchResult ->
        val (in1, reg) = m.destructured
        Instruction.Copy(in1, reg)
    }
)

/**
 * Parses a string into an [Instruction].
 */
private fun String.toInstruction(): Instruction? {
    for ((regex, factory) in instructionRegexes) {
        regex.matchEntire(this)?.let {
            return factory(it)
        }
    }
    "Unsupported instruction: $this".println()
    return null
}


/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
        123 -> x
        456 -> y
        x AND y -> d
        x OR y -> e
        x LSHIFT 2 -> f
        y RSHIFT 2 -> g
        NOT x -> h
        NOT y -> i
    """.trimIndent().lines()

    val circuit = testInput.mapNotNull { it.toInstruction() }.associateBy { it.register }

    check(circuit.resolve("d") == 72)
    check(circuit.resolve("e") == 507)
    check(circuit.resolve("f") == 492)
    check(circuit.resolve("g") == 114)
    check(circuit.resolve("h") == 65412)
    check(circuit.resolve("i") == 65079)
    check(circuit.resolve("x") == 123)
    check(circuit.resolve("y") == 456)
}