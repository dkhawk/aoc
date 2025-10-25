package com.sphericalchickens.aoc2016gem.day12

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

// --- Data Models ---
// No changes were needed for the core data models (Computer, Argument, 
// and Command). They already use sealed interfaces effectively to create
// a robust and type-safe representation of the VM and its instruction set.

/**
 * The Assembunny Virtual Machine.
 * It holds the program (an immutable list of commands),
 * the registers (a mutable array), and the program counter.
 */
private class Computer(val program: List<Command>) {
    val registers = IntArray(4) // 'a', 'b', 'c', 'd'
    var pc = 0

    fun executeProgram() {
        // The execution loop is clean and correct.
        // It continues as long as the program counter is within the
        // bounds of the program.
        while (pc <= program.lastIndex) {
            val next = program[pc]
            // We use the `invoke` operator on the command itself
            // to perform the operation. This is great polymorphic design.
            next(this)
        }
    }
}

/**
 * A sealed interface representing an argument to a command.
 * It can be either an immediate value or a register.
 */
private sealed interface Argument {
    fun resolve(computer: Computer): Int

    data class Immediate(val value: Int) : Argument {
        override fun resolve(computer: Computer): Int {
            return value
        }
    }

    data class Register(val value: Char) : Argument {
        // Caching the index calculation is a good small optimization.
        val index = value - 'a'

        override fun resolve(computer: Computer): Int {
            return computer.registers[index]
        }
    }
}

/**
 * A sealed interface representing a single Assembunny command.
 * Each command knows how to execute itself, including how to
 * modify the program counter.
 */
private sealed interface Command {
    operator fun invoke(computer: Computer)

    data class Copy(val source: Argument, val destination: Argument.Register) : Command {
        override fun invoke(computer: Computer) {
            computer.registers[destination.index] = source.resolve(computer)
            computer.pc += 1
        }
    }

    data class Inc(val destination: Argument.Register) : Command {
        override fun invoke(computer: Computer) {
            computer.registers[destination.index]++
            computer.pc += 1
        }
    }

    data class Dec(val destination: Argument.Register) : Command {
        override fun invoke(computer: Computer) {
            computer.registers[destination.index]--
            computer.pc += 1
        }
    }

    data class Jnz(val reg: Argument, val offset: Argument.Immediate) : Command {
        override fun invoke(computer: Computer) {
            // The Jnz command correctly handles its own PC logic,
            // either jumping or incrementing by 1.
            if (reg.resolve(computer) != 0) {
                computer.pc += offset.resolve(computer)
            } else {
                computer.pc += 1
            }
        }
    }
}

// --- Parsing ---

/**
 * Compiles a list of string instructions into a list of
 * executable Command objects. This is a simple, functional
 * transformation.
 */
private fun compile(input: List<String>): List<Command> {
    return input.map { it.toCommand() }
}

/**
 * Our primary parsing function. It uses a `when` expression to
 * dispatch to the correct Command constructor based on the
 * instruction mnemonic.
 */
private fun String.toCommand(): Command {
    val parts = this.split(" ")
    return when (parts[0]) {
        // We now use our more robust helper functions.
        // `toArgument()` is for "value or register".
        // `toRegisterArgument()` is for "register only".
        // `toImmediateArgument()` is for "immediate only".
        // This makes the parser's intent explicit and removes
        // the need for `!!`.
        "cpy" -> Command.Copy(parts[1].toArgument(), parts[2].toRegisterArgument())
        "inc" -> Command.Inc(parts[1].toRegisterArgument())
        "dec" -> Command.Dec(parts[1].toRegisterArgument())
        "jnz" -> Command.Jnz(parts[1].toArgument(), parts[2].toImmediateArgument())
        else -> error("Unknown command: $this")
    }
}

// Here are our refactored parsing helpers.
// I've renamed them to be clear about *what* they are parsing into.

/**
 * Parses a string (e.g., "a") into a Register argument.
 */
private fun String.toRegisterArgument() = Argument.Register(this[0])

/**
 * Parses a string (e.g., "5") into an Immediate argument.
 * Throws a clear error if the string is not an integer.
 */
private fun String.toImmediateArgument() = Argument.Immediate(
    this.toIntOrNull() ?: error("Expected immediate integer value: $this")
)

/**
 * Parses a string that could be *either* an immediate value or a register.
 * It idiomatically uses `toIntOrNull()` with the `let` scope function
 * and the Elvis operator (`?:`) to fall back to register parsing.
 */
private fun String.toArgument() = this.toIntOrNull()
    ?.let { Argument.Immediate(it) }
    ?: this.toRegisterArgument()


// --- Solution Entry Points ---

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false // No separate test for part 2
    val runPart2Solution = true
    // ----------------------------------------

    // Corrected the day from 11 to 12.
    println("--- Advent of Code 2016, Day 12 ---")

    val input = readInputLines("aoc2016/day12_input.txt")

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
            // The correct answer for my input is 318003
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        // Part 2 just changes an initial value, so a separate logic
        // test isn't strictly necessary.
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input)
            // The correct answer for my input is 9227657
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        cpy 41 a
        inc a
        inc a
        dec a
        jnz a 2
        dec a
    """.trimIndent().lines()
    check("Part 1 Test Case 1", 42, part1(testInput))
}

private fun runPart2Tests() {
    // Stub for completeness
}

// --- Logic Refactoring ---

/**
 * This new `solve` function encapsulates the common logic from
 * both `part1` and `part2`. It handles compiling the program,
 * setting up the computer with an initial (optional) register state,
 * running the program, and returning the result.
 *
 * It defaults to an all-zero register state.
 */
private fun solve(input: List<String>, initialRegisters: IntArray = IntArray(4)): Int {
    val program = compile(input)
    val computer = Computer(program)

    // We must *copy* the initial state into the computer's registers.
    // If we did `computer.registers = initialRegisters`, we would
    // be mutating the default parameter array, which would cause
    // future calls to `solve` to see the mutated state.
    initialRegisters.copyInto(computer.registers)

    computer.executeProgram()
    return computer.registers[0] // Register 'a'
}

/**
 * Part 1 now becomes a simple, declarative call to `solve`
 * using the default (all-zero) register state.
 */
private fun part1(input: List<String>): Int {
    return solve(input)
}

/**
 * Part 2 also calls `solve`, but first it constructs the
 * specific initial state required (register 'c' = 1).
 *
 * Using `apply` here is a concise, idiomatic way to create
 * and modify the array in a single expression block.
 */
private fun part2(input: List<String>): Int {
    val initialRegs = IntArray(4).apply {
        this['c' - 'a'] = 1
    }
    return solve(input, initialRegs)
}