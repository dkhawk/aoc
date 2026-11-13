package com.sphericalchickens.aoc2018gem.day16

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputText
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2018, Day 16: Chronal Classification (Refactored)
 *
 * This solution reverse-engineers a 4-register, 16-opcode virtual machine from observed
 * before/after register snapshots and then executes a test program using the deduced opcode map.
 *
 * ## Literate Walkthrough
 *
 * ### 1. Modeling the 16 Operations
 * Every instruction in the Chronal CPU takes three operands `(a, b, c)`, reads from registers or
 * immediate values depending on the opcode, and writes a single integer result to register `c`.
 * Modeling [Operation] as an `enum class` whose constructor accepts a `(a, b, regs) -> Int`
 * lambda centralizes the immutable copy-and-write step in one place, eliminates 16 repetitive
 * subclasses, and provides `Operation.entries` automatically.
 *
 * ### 2. Unified Input Parsing
 * The puzzle input has two sections separated by four consecutive newlines (`\n\n\n\n`):
 * 1. **Samples:** `Before: [...]`, an `Instruction`, and `After: [...]`.
 * 2. **Test Program:** A sequence of `Instruction` lines using the same 4-integer format.
 *
 * Normalizing `\r\n` line endings and sharing [toInstruction] across both sections avoids
 * duplicated parsing logic, while `require` checks ensure malformed inputs fail loudly.
 *
 * ### 3. Part 1 & Part 2 (Constraint Propagation)
 * - **Part 1** counts how many [Sample]s are consistent with 3 or more [Operation]s.
 * - **Part 2** initializes each numeric opcode `0..15` with all 16 candidate operations,
 *   intersects each opcode's candidates with the operations matching its observed samples, and
 *   then iteratively resolves any opcode narrowed down to a single candidate—removing that
 *   operation from all remaining unresolved opcodes—before folding the test program over `[0, 0, 0, 0]`.
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

/**
 * The 16 operations supported by the Chronal CPU.
 *
 * Each entry defines only how its output value is computed from `(a, b, regs)`; register copy-on-write
 * semantics are handled uniformly in [apply].
 */
enum class Operation(private val compute: (a: Int, b: Int, regs: List<Int>) -> Int) {
    ADDR({ a, b, regs -> regs[a] + regs[b] }),
    ADDI({ a, b, regs -> regs[a] + b }),
    MULR({ a, b, regs -> regs[a] * regs[b] }),
    MULI({ a, b, regs -> regs[a] * b }),
    BANR({ a, b, regs -> regs[a] and regs[b] }),
    BANI({ a, b, regs -> regs[a] and b }),
    BORR({ a, b, regs -> regs[a] or regs[b] }),
    BORI({ a, b, regs -> regs[a] or b }),
    SETR({ a, _, regs -> regs[a] }),
    SETI({ a, _, _ -> a }),
    GTIR({ a, b, regs -> if (a > regs[b]) 1 else 0 }),
    GTRI({ a, b, regs -> if (regs[a] > b) 1 else 0 }),
    GTRR({ a, b, regs -> if (regs[a] > regs[b]) 1 else 0 }),
    EQIR({ a, b, regs -> if (a == regs[b]) 1 else 0 }),
    EQRI({ a, b, regs -> if (regs[a] == b) 1 else 0 }),
    EQRR({ a, b, regs -> if (regs[a] == regs[b]) 1 else 0 });

    /** Returns a new register list with the result of this operation written to register [c]. */
    fun apply(a: Int, b: Int, c: Int, regs: List<Int>): List<Int> =
        regs.toMutableList().apply { this[c] = compute(a, b, regs) }

    /** Executes [instruction] against [regs] and returns the updated register state. */
    fun apply(instruction: Instruction, regs: List<Int>): List<Int> =
        apply(instruction.a, instruction.b, instruction.c, regs)
}

/** A 4-integer VM instruction `(op, a, b, c)`. */
data class Instruction(val op: Int, val a: Int, val b: Int, val c: Int)

/** An observed before/after register snapshot for a single [instruction]. */
data class Sample(
    val instruction: Instruction,
    val before: List<Int>,
    val after: List<Int>
) {
    /** Returns the set of all [Operation]s whose behavior matches this sample. */
    fun matchingOperations(): Set<Operation> =
        Operation.entries.filterTo(mutableSetOf()) { op ->
            op.apply(instruction, before) == after
        }
}

/** Parsed representation of the Day 16 puzzle input. */
data class PuzzleInput(val samples: List<Sample>, val program: List<Instruction>)

/**
 * Counts how many samples in [input] behave like three or more opcodes.
 */
fun part1(input: String): Int =
    parseInput(input).samples.count { it.matchingOperations().size >= 3 }

/**
 * Deduces the opcode-to-[Operation] mapping from the samples and executes the test program,
 * returning the final value in register `0`.
 */
fun part2(input: String): Int {
    val (samples, program) = parseInput(input)

    // Narrow each opcode's candidate set using every observed sample.
    val candidates: MutableMap<Int, MutableSet<Operation>> =
        (0..15).associateWith { Operation.entries.toMutableSet() }.toMutableMap()

    for (sample in samples) {
        candidates.getValue(sample.instruction.op).retainAll(sample.matchingOperations())
    }

    // Iteratively peel off opcodes with a single remaining candidate.
    val resolved = mutableMapOf<Int, Operation>()
    while (candidates.isNotEmpty()) {
        val determined = candidates.filterValues { it.size == 1 }
        check(determined.isNotEmpty()) { "Cannot uniquely resolve remaining opcodes: $candidates" }

        for ((opCode, ops) in determined) {
            val op = ops.single()
            resolved[opCode] = op
            candidates.remove(opCode)
            candidates.values.forEach { it.remove(op) }
        }
    }

    val finalRegs = program.fold(listOf(0, 0, 0, 0)) { regs, instruction ->
        resolved.getValue(instruction.op).apply(instruction, regs)
    }
    return finalRegs[0]
}

// ---------------------------------------------------------------------------------------------
// Parsing & Utilities
// ---------------------------------------------------------------------------------------------

private fun parseInput(raw: String): PuzzleInput {
    val normalized = raw.replace("\r\n", "\n").trim()
    val sections = normalized.split("\n\n\n\n", limit = 2)
    require(sections.size == 2) { "Expected samples and program separated by 4 newlines" }

    val samples = sections[0].split("\n\n").map { it.toSample() }
    val program = sections[1].lineSequence()
        .filter { it.isNotBlank() }
        .map { it.toInstruction() }
        .toList()

    return PuzzleInput(samples, program)
}

private fun String.toSample(): Sample {
    val lines = lines()
    require(lines.size == 3) { "Expected 3 lines in sample, got ${lines.size}" }
    require(lines[0].startsWith("Before: ")) { "Invalid Before line: ${lines[0]}" }
    require(lines[2].startsWith("After: ")) { "Invalid After line: ${lines[2]}" }

    return Sample(
        instruction = lines[1].toInstruction(),
        before = lines[0].parseRegisterList(),
        after = lines[2].parseRegisterList()
    )
}

private fun String.parseRegisterList(): List<Int> =
    substringAfter("[").substringBefore("]").split(",").map { it.trim().toInt() }

private fun String.toInstruction(): Instruction {
    val parts = trim().split(Regex("\\s+")).map { it.toInt() }
    require(parts.size == 4) { "Expected 4 integers in instruction, got ${parts.size}: '$this'" }
    return Instruction(parts[0], parts[1], parts[2], parts[3])
}

// ---------------------------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------------------------

private fun runTests() {
    // Basic operation checks
    check("ADDR", listOf(1, 2, 3, 4) == Operation.ADDR.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("ADDI", listOf(1, 2, 12, 4) == Operation.ADDI.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("MULR", listOf(1, 2, 2, 4) == Operation.MULR.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("MULI", listOf(1, 2, 20, 4) == Operation.MULI.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("BANR (zero)", listOf(1, 2, 0, 4) == Operation.BANR.apply(0, 1, 2, listOf(1, 2, 9, 4)))
    check("BANR (non-zero)", listOf(3, 6, 2, 4) == Operation.BANR.apply(0, 1, 2, listOf(3, 6, 0, 4)))
    check("BANI", listOf(1, 2, 2, 4) == Operation.BANI.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("BORR", listOf(1, 2, 3, 4) == Operation.BORR.apply(0, 1, 2, listOf(1, 2, 0, 4)))
    check("BORI", listOf(1, 2, 10, 4) == Operation.BORI.apply(1, 10, 2, listOf(1, 2, 0, 4)))

    check("SETR (ignores b)", listOf(1, 2, 1, 4) == Operation.SETR.apply(0, 99, 2, listOf(1, 2, 0, 4)))
    check("SETI (ignores b)", listOf(1, 2, 42, 4) == Operation.SETI.apply(42, 99, 2, listOf(1, 2, 0, 4)))

    // Greater-than checks: >, ==, and <
    check("GTIR (>)", listOf(1, 2, 1, 4) == Operation.GTIR.apply(15, 1, 2, listOf(1, 2, 0, 4)))
    check("GTIR (==)", listOf(1, 2, 0, 4) == Operation.GTIR.apply(2, 1, 2, listOf(1, 2, 9, 4)))
    check("GTIR (<)", listOf(1, 2, 0, 4) == Operation.GTIR.apply(-5, 1, 2, listOf(1, 2, 9, 4)))

    check("GTRI (>)", listOf(1, 2, 1, 4) == Operation.GTRI.apply(3, -10, 2, listOf(1, 2, 0, 4)))
    check("GTRI (==)", listOf(1, 2, 0, 4) == Operation.GTRI.apply(1, 2, 2, listOf(1, 2, 9, 4)))
    check("GTRI (<)", listOf(1, 2, 0, 4) == Operation.GTRI.apply(1, 99, 2, listOf(1, 2, 9, 4)))

    check("GTRR (>)", listOf(1, 2, 1, 4) == Operation.GTRR.apply(1, 0, 2, listOf(1, 2, 0, 4)))
    check("GTRR (==)", listOf(2, 2, 0, 4) == Operation.GTRR.apply(0, 1, 2, listOf(2, 2, 9, 4)))
    check("GTRR (<)", listOf(1, 2, 0, 4) == Operation.GTRR.apply(0, 1, 2, listOf(1, 2, 9, 4)))

    // Equality checks: == and !=
    check("EQIR (true)", listOf(1, 25, 1, 4) == Operation.EQIR.apply(25, 1, 2, listOf(1, 25, 0, 4)))
    check("EQIR (false)", listOf(1, 2, 0, 4) == Operation.EQIR.apply(25, 1, 2, listOf(1, 2, 9, 4)))

    check("EQRI (true)", listOf(1, 25, 1, 4) == Operation.EQRI.apply(1, 25, 2, listOf(1, 25, 0, 4)))
    check("EQRI (false)", listOf(1, 2, 0, 4) == Operation.EQRI.apply(1, 25, 2, listOf(1, 2, 9, 4)))

    check("EQRR (true)", listOf(2, 1, 2, 1) == Operation.EQRR.apply(0, 2, 3, listOf(2, 1, 2, 4)))
    check("EQRR (false)", listOf(1, 2, 0, 4) == Operation.EQRR.apply(0, 1, 2, listOf(1, 2, 9, 4)))

    // Self-register aliasing checks (a == b == c)
    check("ADDR self", listOf(5, 14, 3, 4) == Operation.ADDR.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("MULR self", listOf(5, 49, 3, 4) == Operation.MULR.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("GTRR self", listOf(5, 0, 3, 4) == Operation.GTRR.apply(1, 1, 1, listOf(5, 7, 3, 4)))
    check("EQRR self", listOf(5, 1, 3, 4) == Operation.EQRR.apply(1, 1, 1, listOf(5, 7, 3, 4)))

    // Immutability check
    val originalRegs = mutableListOf(1, 2, 99, 4)
    val resultRegs = Operation.ADDR.apply(0, 1, 2, originalRegs)
    check("Input regs not mutated", listOf(1, 2, 99, 4) == originalRegs && listOf(1, 2, 3, 4) == resultRegs)

    // AoC problem statement sample
    val sampleText = """
        Before: [3, 2, 1, 1]
        9 2 1 2
        After:  [3, 2, 2, 1]
    """.trimIndent()
    val sample = sampleText.toSample()
    check(
        "Sample matches MULR, ADDI, SETI",
        setOf(Operation.MULR, Operation.ADDI, Operation.SETI) == sample.matchingOperations()
    )
    check("Part 1 sample count", 1, part1("$sampleText\n\n\n\n9 2 1 2"))
}

