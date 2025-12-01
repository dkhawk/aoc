package com.sphericalchickens.aoc2016.day25

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Solution = true
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 25 ---")

    val input = readInputLines("aoc2016/day25_input.txt")

    // --- Part 1 ---
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input)
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private class Computer(val program: MutableList<Command>) {
    val registers = IntArray(4)
    var pc = 0
    val terminal = mutableListOf<Int>()

    var commandsExecuted = 0L

    lateinit var lastCommand: Command

    fun executeProgram(function: (Computer) -> Boolean) {
        while (pc <= program.lastIndex) {
            commandsExecuted += 1
            val next = program[pc]
            next.invoke(this)

            lastCommand = next

            if (function(this)) {
                break
            }
        }
    }

    fun replaceInstruction(instructionPointer: Int, newCommand: Command) {
        program[instructionPointer] = newCommand
    }

    override fun toString(): String {
        return "pc: $pc ${program[pc]}  ${registers.withIndex().joinToString(", ") { (i, v) -> "${'a' + i}: ${v.toString().padStart(5)}"}}"
    }

    fun output(v: Int) {
        terminal.addLast(v)
    }

    fun reset() {
        for (i in registers.indices) {
            registers[i] = 0
        }
        pc = 0
        terminal.clear()
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
    fun toggle(): Command

    data class Copy(val source: Argument, val destination: Argument) : Command {
        override fun invoke(computer: Computer) {
            if (destination !is Argument.Register) {
                error("For Copy, expected destination to be a register: $destination")
            }
            computer.registers[destination.index] = source.resolve(computer)
            computer.pc += 1
        }

        override fun toggle() = Jnz(source, destination)
    }

    data class Inc(val destination: Argument) : Command {
        override fun invoke(computer: Computer) {
            if (destination !is Argument.Register) {
                error("For Inc, expected destination to be a register: $destination")
            }
            computer.registers[destination.index]++
            computer.pc += 1
        }

        override fun toggle() = Dec(destination)
    }

    data class Dec(val destination: Argument) : Command {
        override fun invoke(computer: Computer) {
            if (destination !is Argument.Register) {
                error("For Dec, expected destination to be a register: $destination")
            }
            computer.registers[destination.index]--
            computer.pc += 1
        }

        override fun toggle() = Inc(destination)
    }

    data class Jnz(val reg: Argument, val offset: Argument) : Command {
        override fun invoke(computer: Computer) {
            if (reg.resolve(computer) != 0) {
                computer.pc += offset.resolve(computer)
            } else {
                computer.pc += 1
            }
        }

        override fun toggle(): Command {
            return Copy(reg, offset)
        }
    }

    data class Tgl(val offset: Argument) : Command {
        override fun invoke(computer: Computer) {
            val instructionPointer = offset.resolve(computer) + computer.pc

            if (instructionPointer in 0..(computer.program.lastIndex)) {
                println(computer.toString())
                // Valid instruction
                val newCommand = computer.program[instructionPointer].toggle()
                computer.replaceInstruction(instructionPointer, newCommand)
            }

            computer.pc += 1
        }

        override fun toggle() = Inc(offset)
    }

    data class Out(val arg: Argument) : Command {
        override fun invoke(computer: Computer) {
            val v = arg.resolve(computer)
            computer.output(v)
        }

        override fun toggle(): Command {
            TODO("Not yet implemented")
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
        "jnz" -> Command.Jnz(parts[1].toArgument(), parts[2].toArgument())
        "tgl" -> Command.Tgl(parts[1].toArgument())
        "out" -> Command.Out(parts[1].toArgument())
        else -> error("wat? $this")
    }
}

private fun String.toRegister() = Argument.Register(this[0])

private fun String.toImmediate() = toIntOrNull()?.let { Argument.Immediate(it) }

private fun String.toArgument() = this.toImmediate() ?: this.toRegister()


private fun part1(input: List<String>): Int {
    val program = compile(input)

//    var target = 0
//
//    repeat(15) { i ->
//        target = target shl 1 or ((i) % 2)
//    }
//
//    target.toString(2).println()
//
//    target.println()
//
//    return (target - 2532)

    for (i in 10922.. 109220) {
//        val a = i
//        val d = a + 2532
//
//        d.toString(2).println()

        val computer = Computer(program.toMutableList())
        computer.registers[0] = i

        computer.executeProgram() { computer ->
            computer.commandsExecuted > 100_000 || if (computer.lastCommand is Command.Out) {
                computer.terminal.size == 16 && computer.terminal.joinToString("").startsWith("01010101")
            } else {
                false
            }
        }
//
        if (computer.terminal.joinToString("").startsWith("010101010101010")) {
            return i
        }
    }

    error("Correct value not found")
}

private fun part2(input: List<String>): Int {
    val program = compile(input)

    val computer = Computer(program.toMutableList())
    computer.registers['a' - 'a'] = 12
    computer.executeProgram { computer ->
        false
    }

    return computer.registers[0]
}
