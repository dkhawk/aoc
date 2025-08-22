package com.sphericalchickens.aoc2015.day07

import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 07: TBD
 *
 * This program solves the puzzle for Day 07.
 * For example:
 *
 * 123 -> x means that the signal 123 is provided to wire x.
 * x AND y -> z means that the bitwise AND of wire x and wire y is provided to wire z.
 * p LSHIFT 2 -> q means that the value from wire p is left-shifted by 2 and then provided to wire q.
 * NOT e -> f means that the bitwise complement of the value from wire e is provided to wire f.
 * Other possible gates include OR (bitwise OR) and RSHIFT (right-shift). If, for some reason, you'd like to emulate the circuit instead, almost all programming languages (for example, C, JavaScript, or Python) provide operators for these gates.
 *
 * For example, here is a simple circuit:
 *
 * 123 -> x
 * 456 -> y
 * x AND y -> d
 * x OR y -> e
 * x LSHIFT 2 -> f
 * y RSHIFT 2 -> g
 * NOT x -> h
 * NOT y -> i
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day07_input.txt")
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

fun part1(input: List<String>): Int {
    val values = mutableMapOf<String, Int>()
    val circuit = input.mapNotNull { it.toInstruction() }.associateBy { it.register }

    return resolveRegister("a", circuit, values)
}

fun part2(input: List<String>): Int {
    val values = mutableMapOf<String, Int>()
    val circuit = input.mapNotNull { it.toInstruction() }.associateBy { it.register }

    values["b"] = 3176

    return resolveRegister("a", circuit, values)
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    val testInput1 = """
        123 -> x
        456 -> y
        x AND y -> d
        x OR y -> e
        x LSHIFT 2 -> f
        y RSHIFT 2 -> g
        NOT x -> h
        NOT y -> i""".trimIndent().lines()
    check(testInput1[0].toInstruction() == Instruction.Copy("123", "x"))
    check(testInput1[2].toInstruction() == Instruction.And("x", "y", "d"))
    check(testInput1[3].toInstruction() == Instruction.Or("x", "y", "e"))
    check(testInput1[4].toInstruction() == Instruction.Lshift("x", "2", "f"))
    check(testInput1[5].toInstruction() == Instruction.Rshift("y", "2", "g"))
    check(testInput1[6].toInstruction() == Instruction.Not("x", "h"))

    val values = mutableMapOf<String, Int>()
    val circuit = testInput1.mapNotNull { it.toInstruction() }.associateBy { it.register }

    check(resolveRegister("i", circuit, values) == 65079)
    check(resolveRegister("d", circuit, values) == 72)
    check(resolveRegister("e", circuit, values) == 507)
    check(resolveRegister("f", circuit, values) == 492)

    check("lx -> a".toInstruction() == Instruction.Copy(in1 = "lx", register = "a"))
}

private fun resolveRegister(register: String, circuit: Map<String, Instruction>, values: MutableMap<String, Int>): Int {
    val instruction = circuit.getValue(register)

    val value = values[register] ?: instruction(values) { dep : String ->
        resolveRegister(dep, circuit, values)
    }

    values[register] = value

    return value
}

private sealed class Instruction {
    abstract val register: String
    abstract operator fun invoke(values: Map<String, Int>, function: (String) -> Int): Int

    data class And(val in1: String, val in2: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            val v2 = in2.toIntOrNull() ?: values[in2] ?: function(in2)

            return v1 and v2 and 0xFFFF
        }
    }

    data class Or(val in1: String, val in2: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            val v2 = in2.toIntOrNull() ?: values[in2] ?: function(in2)

            return v1 or v2 and 0xFFFF
        }
    }

    data class Lshift(val in1: String, val in2: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            val v2 = in2.toIntOrNull() ?: values[in2] ?: function(in2)

            return v1.shl(v2) and 0xFFFF
        }
    }

    data class Rshift(val in1: String, val in2: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            val v2 = in2.toIntOrNull() ?: values[in2] ?: function(in2)

            return v1.shr(v2) and 0xFFFF
        }
    }

    data class Not(val in1: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            return v1.inv() and 0xFFFF
        }
    }

    data class Copy(val in1: String, override val register: String) : Instruction() {
        override fun invoke(values: Map<String, Int>, function: (String) -> Int): Int {
            val v1 = in1.toIntOrNull() ?: values[in1] ?: function(in1)
            return v1
        }
    }
}

private fun String.toInstruction(): Instruction? {
    val instructionRegex = Regex("""((?<literal>\w+)|((?<input1>\w+)? ?(?<op>AND|OR|LSHIFT|RSHIFT|NOT) (?<input2>\w+)?)) -> (?<register>\w+)$""")
    val matchResult = instructionRegex.find(this)

    if (matchResult != null) {
        val register = matchResult.groups["register"]!!.value

        val op = matchResult.groups["op"]?.value
        return when (op) {
            null -> {
                val literal = matchResult.groups["literal"]!!.value.trim()
                Instruction.Copy(literal, register)
            }
            "AND" -> Instruction.And(
                matchResult.groups["input1"]!!.value,
                matchResult.groups["input2"]!!.value,
                register
            )
            "OR" -> Instruction.Or(
                matchResult.groups["input1"]!!.value,
                matchResult.groups["input2"]!!.value,
                register
            )
            "LSHIFT" -> Instruction.Lshift(
                matchResult.groups["input1"]!!.value,
                matchResult.groups["input2"]!!.value,
                register
            )
            "RSHIFT" -> Instruction.Rshift(
                matchResult.groups["input1"]!!.value,
                matchResult.groups["input2"]!!.value,
                register
            )
            "NOT" -> Instruction.Not(
                matchResult.groups["input2"]!!.value,
                register
            )
            else -> {
                "Unsupported instruction: $this".println()
                null
            }
        }
    }

    return null
}
