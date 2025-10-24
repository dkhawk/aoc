package com.sphericalchickens.aoc2016.day11

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

private class Computer(val program: List<Command>) {
    val registers = IntArray(4)
    var pc = 0

    fun executeProgram() {
        while (pc <= program.lastIndex) {
            val next = program[pc]
            next.invoke(this)
        }
    }
}

private sealed interface Argument {
    fun resolve(computer: Computer): Int

    data class Immediate(val value: Int) : Argument {
        override fun resolve(computer: Computer): Int {
            return value
        }
    }

    data class Register(val value: Char) : Argument {
        val index = value - 'a'

        override fun resolve(computer: Computer): Int {
            return computer.registers[index]
        }
    }
}

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
            if (reg.resolve(computer) != 0) {
                computer.pc += offset.resolve(computer)
            } else {
                computer.pc += 1
            }
        }
    }
}

private fun compile(input: List<String>) : List<Command> {
    return input.map { it.toCommand() }
}

private fun String.toCommand(): Command {
    val parts = this.split(" ")
    return when (parts[0]) {
        "cpy" -> Command.Copy(parts[1].toArgument(), parts[2].toRegister())
        "inc" -> Command.Inc(parts[1].toRegister())
        "dec" -> Command.Dec(parts[1].toRegister())
        "jnz" -> Command.Jnz(parts[1].toArgument(), parts[2].toImmediate()!!)
        else -> error("wat? $this")
    }
}

private fun String.toRegister() = Argument.Register(this[0])

private fun String.toImmediate() = toIntOrNull()?.let { Argument.Immediate(it) }

private fun String.toArgument() = this.toImmediate() ?: this.toRegister()

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 11 ---")

    val input = readInputLines("aoc2016/day11_input.txt")

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
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun part1(input: List<String>): Int {
    val program = compile(input)

    val computer = Computer(program)
    computer.executeProgram()

    return computer.registers[0]
}

private fun part2(input: List<String>): Int {
    val program = compile(input)

    val computer = Computer(program)
    computer.registers['c' - 'a'] = 1
    computer.executeProgram()

    return computer.registers[0]
}
