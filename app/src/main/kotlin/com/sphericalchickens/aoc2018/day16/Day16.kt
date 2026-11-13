package com.sphericalchickens.aoc2018.day16

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputText
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
    val puzzleInput = readInputText("aoc2018/day16_input.txt")
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

sealed class Operation {
    abstract fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int>
}

data object Addr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] + regs[b]
        return out
    }
}

data object Addi : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] + b
        return out
    }
}

data object Mulr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] * regs[b]
        return out
    }
}

data object Muli : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] * b
        return out
    }
}

data object Banr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] and regs[b]
        return out
    }
}

data object Bani : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] and b
        return out
    }
}

data object Borr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] or regs[b]
        return out
    }
}

data object Bori : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a] or b
        return out
    }
}

data object Setr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = regs[a]
        return out
    }
}

data object Seti : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = a
        return out
    }
}

data object Gtir : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (a > regs[b]) 1 else 0
        return out
    }
}

data object Gtri : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (regs[a] > b) 1 else 0
        return out
    }
}

data object Gtrr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (regs[a] > regs[b]) 1 else 0
        return out
    }
}

data object Eqir : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (a == regs[b]) 1 else 0
        return out
    }
}

data object Eqri : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (regs[a] == b) 1 else 0
        return out
    }
}

data object Eqrr : Operation() {
    override fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> {
        val out = regs.toMutableList()
        out[c] = if (regs[a] == regs[b]) 1 else 0
        return out
    }
}

val allOperations = listOf(
    Addr, Addi, Mulr, Muli, Banr, Bani, Borr, Bori,
    Setr, Seti, Gtir, Gtri, Gtrr, Eqir, Eqri, Eqrr
)

fun part1(input: String): Int {
    // We only want the first part
    val part1Text = input.substringBefore("\n\n\n\n")

    return part1Text.split("\n\n").count { probText ->
        numberOfOpcode(probText.toProblem()) >= 3
    }
}

fun part2(input: String): Int {
    val part1Text = input.substringBefore("\n\n\n\n")

    val opMap = (0..15).map { opCode ->
        allOperations.toMutableSet()
    }

    part1Text.split("\n\n").forEach { probText ->
        val x = toOpcodes(probText.toProblem())
        val (opCodes, operations) = x.unzip()
        val opCode = opCodes.first()
        opMap[opCode].removeIf { it !in operations }
    }

    val unresolved = opMap.withIndex().associate { it.index to it.value }.toMutableMap()
    val resolved = hashMapOf<Int, Operation>()

    while (unresolved.isNotEmpty()) {
        // Find the next op that has one value
        val resolvable = unresolved.filterValues { it.size == 1 }
        if (resolvable.isEmpty()) {
            error("Unresolvable: $unresolved")
        }

        resolvable.forEach { (i, operations) ->
            val thisOp = operations.first()
            resolved[i] = thisOp
            unresolved.remove(i)
            unresolved.forEach { (i, operations) -> operations.remove(thisOp) }
        }
    }

    // Now run the damn program
    val part2Text = input.substringAfter("\n\n\n\n")

    val regs = part2Text.split("\n").filter { it.isNotBlank() }.fold(listOf(0, 0, 0, 0)) { regs, line ->
        val (opCode, a, b, c) = line.split(" ").map { it.trim().toInt() }
        resolved[opCode]!!.apply(a, b, c, regs)
    }

    return regs[0]
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Basic operation checks
    check("Addr", listOf(1, 2, 3, 4) == Addr.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("Addi", listOf(1, 2, 12, 4) == Addi.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("Mulr", listOf(1, 2, 2, 4) == Mulr.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("Muli", listOf(1, 2, 20, 4) == Muli.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("Banr (zero)", listOf(1, 2, 0, 4) == Banr.apply(0, 1, 2, listOf(1, 2, 9, 4)))
    check("Banr (non-zero)", listOf(3, 6, 2, 4) == Banr.apply(0, 1, 2, listOf(3, 6, 0, 4)))
    check("Bani", listOf(1, 2, 2, 4) == Bani.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("Borr", listOf(1, 2, 3, 4) == Borr.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("Bori", listOf(1, 2, 10, 4) == Bori.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("Setr (ignores b out of range)", listOf(1, 2, 1, 4) == Setr.apply(0, 99, 2, listOf(1, 2, 0, 4)))
    check("Seti (immediate a & ignores b out of range)", listOf(1, 2, 42, 4) == Seti.apply(42, 99, 2, listOf(1, 2, 0, 4)))

    // Greater-than checks: >, ==, and < (to catch >= or swapped operands)
    check("Gtir (>)", listOf(1, 2, 1, 4) == Gtir.apply(15, 1, 2, listOf(1, 2, 0, 4)))
    check("Gtir (==)", listOf(1, 2, 0, 4) == Gtir.apply(2, 1, 2, listOf(1, 2, 9, 4)))
    check("Gtir (<)", listOf(1, 2, 0, 4) == Gtir.apply(-5, 1, 2, listOf(1, 2, 9, 4)))

    check("Gtri (>)", listOf(1, 2, 1, 4) == Gtri.apply(3, -10, 2, listOf(1, 2, 0, 4)))
    check("Gtri (==)", listOf(1, 2, 0, 4) == Gtri.apply(1, 2, 2, listOf(1, 2, 9, 4)))
    check("Gtri (<)", listOf(1, 2, 0, 4) == Gtri.apply(1, 99, 2, listOf(1, 2, 9, 4)))

    check("Gtrr (>)", listOf(1, 2, 1, 4) == Gtrr.apply(1, 0, 2, listOf(1, 2, 0, 4)))
    check("Gtrr (==)", listOf(2, 2, 0, 4) == Gtrr.apply(0, 1, 2, listOf(2, 2, 9, 4)))
    check("Gtrr (<)", listOf(1, 2, 0, 4) == Gtrr.apply(0, 1, 2, listOf(1, 2, 9, 4)))

    // Equality checks: == and != (with immediate values outside 0..3 to verify no register lookup on immediates)
    check("Eqir (true)", listOf(1, 25, 1, 4) == Eqir.apply(25, 1, 2, listOf(1, 25, 0, 4)))
    check("Eqir (false)", listOf(1, 2, 0, 4) == Eqir.apply(25, 1, 2, listOf(1, 2, 9, 4)))

    check("Eqri (true)", listOf(1, 25, 1, 4) == Eqri.apply(1, 25, 2, listOf(1, 25, 0, 4)))
    check("Eqri (false)", listOf(1, 2, 0, 4) == Eqri.apply(1, 25, 2, listOf(1, 2, 9, 4)))

    check("Eqrr (true)", listOf(2, 1, 2, 1) == Eqrr.apply(0, 2, 3, listOf(2, 1, 2, 4)))
    check("Eqrr (false)", listOf(1, 2, 0, 4) == Eqrr.apply(0, 1, 2, listOf(1, 2, 9, 4)))

    // Self-register aliasing checks (a == b, c == a, c == b)
    check("Addr self (a == b == c)", listOf(5, 14, 3, 4) == Addr.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("Mulr self (a == b == c)", listOf(5, 49, 3, 4) == Mulr.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("Gtrr self (a == b == c)", listOf(5, 0, 3, 4) == Gtrr.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("Eqrr self (a == b == c)", listOf(5, 1, 3, 4) == Eqrr.apply(1, 1, 1, listOf(5, 7, 3, 4)))

    // Immutability check: input register list is not modified
    val originalRegs = mutableListOf(1, 2, 99, 4)
    val resultRegs = Addr.apply(0, 1, 2, originalRegs)
    check("Input regs not mutated", listOf(1, 2, 99, 4) == originalRegs && listOf(1, 2, 3, 4) == resultRegs)

    // AoC problem statement example:
    // Before: [3, 2, 1, 1], Instruction: 9 2 1 2 (a=2, b=1, c=2), After: [3, 2, 2, 1]
    // Must match exactly 3 operations: Mulr, Addi, and Seti.
    val matchingOps = allOperations.filter { op ->
        op.apply(2, 1, 2, listOf(3, 2, 1, 1)) == listOf(3, 2, 2, 1)
    }.toSet()
    check("Sample matches exactly Mulr, Addi, Seti", setOf(Mulr, Addi, Seti) == matchingOps)

    val testInputs = """
    Before: [3, 2, 1, 1]
    9 2 1 2
    After:  [3, 2, 2, 1]
    
    """.trimIndent().split("\n\n")

    check("Split", testInputs.size == 1)

    val prob = testInputs[0].toProblem()

    check("Solve", 3, numberOfOpcode(prob))
}

private fun numberOfOpcode(prob: Problem): Int {
    return toOpcodes(prob).size
}

private fun toOpcodes(prob: Problem): List<Pair<Int, Operation>> {
    return allOperations.mapNotNull { op ->
        if (
            op.apply(prob.mystery.a, prob.mystery.b, prob.mystery.c, prob.input) == prob.output
        ) {
            prob.mystery.op to op
        } else {
            null
        }
    }
}

data class MysteryOperation(val op: Int, val a: Int, val b: Int, val c: Int)

data class Problem(val mystery: MysteryOperation, val input: List<Int>, val output: List<Int>)

private fun String.toProblem(): Problem {
    val lines = this.split('\n')
    assert(lines[0].startsWith("Before: "))
    val input = lines[0].substringAfter("[").substringBefore("]").split(",").map { it.trim().toInt() }
    assert(lines[2].startsWith("After: "))
    val output = lines[2].substringAfter("[").substringBefore("]").split(",").map { it.trim().toInt() }
    val operation = lines[1].split(" ").map { it.trim().toInt() }

    val (opcode, a, b, c) = operation

    val op = MysteryOperation(opcode, a, b, c)
    return Problem(op, input, output)
}
