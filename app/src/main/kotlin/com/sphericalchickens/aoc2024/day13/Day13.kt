package com.sphericalchickens.aoc2024.day13

import com.sphericalchickens.utils.*



import kotlin.math.min
import kotlin.math.max

val testInput = """
    Button A: X+94, Y+34
    Button B: X+22, Y+67
    Prize: X=8400, Y=5400

    Button A: X+26, Y+66
    Button B: X+67, Y+21
    Prize: X=12748, Y=12176

    Button A: X+17, Y+86
    Button B: X+84, Y+37
    Prize: X=7870, Y=6450

    Button A: X+69, Y+23
    Button B: X+27, Y+71
    Prize: X=18641, Y=10279
""".trimIndent().lines()

val buttonACost = 3
val buttonBCost = 1

val costBasis = VectorLong(buttonACost.toLong(), buttonBCost.toLong())

data class Machine(
    val btnA: VectorLong,
    val btnB: VectorLong,
    val prize: VectorLong,
)

fun main() {
    val testMachines = parseInput(testInput)
    val realMachines = parseInput(readLines("inputs/13"))

    check(part1(testMachines) == 480L)
    "25751 is correct!!".println()
    part1(realMachines).println()

    "108496818472493 is too low!".println()
    "108528956728655 is correct!!".println()
    part2(realMachines).println()
}

private fun Machine.fixUnitError(): Machine {
    val newPrize = VectorLong(prize.x + 10000000000000L, prize.y + 10000000000000L)
    return this.copy(prize = newPrize)
}

private fun Machine.solveLong(limit: Long? = null): Long? {
    val (i, j) = btnA
    val (k, l) = btnB
    val (m, n) = prize

    val aPresses = ((k * n) - (m * l)) / ((k * j) - (l * i))
    val bPresses = (m - (aPresses * i)) / k

    if (limit != null) {
        if (aPresses > limit || bPresses > limit) {
            return null
        }
    }

    return if ((btnA * aPresses) + (btnB * bPresses) == prize) {
        VectorLong(aPresses, bPresses).entrywiseProduct(costBasis).sum()
    } else {
        null
    }
}

private fun part1(machines: List<Machine>): Long {
    return machines.mapNotNull { machine ->
        //  playMachine(machine)
        machine.solveLong(100)
    }.fold(0L) { a, b -> a + b }
}

// Original part 1 solution
fun playMachine(machine: Machine): Long? {
    val maxBPresses = max(100L,
        min(machine.prize.x / machine.btnB.x, machine.prize.y / machine.btnB.y))

    return (0L..maxBPresses).firstOrNull { bPresses ->
        val target = machine.prize - (machine.btnB * bPresses)
        val aPresses = (target.x / machine.btnA.x)

        (aPresses <= 100) && (machine.btnA * aPresses == target)
    }?.let { bPresses ->
        val target = machine.prize - (machine.btnB * bPresses)
        val aPresses = (target.x / machine.btnA.x)
        val aPresses2 = (target.y / machine.btnA.y)

        check(aPresses == aPresses2)
        check(aPresses <= 100)
        check(bPresses <= 100)

        check(target.x.rem(machine.btnA.x) == 0L)
        check(target.y.rem(machine.btnA.y) == 0L)

        VectorLong(target.x / machine.btnA.x, bPresses).entrywiseProduct(costBasis).sum()
//        (target.x / machine.btnA.x) * buttonACost + (bPresses * buttonBCost)
    }
}

private fun part2(machines: List<Machine>): Long {
    return machines.mapNotNull { machine ->
        machine.fixUnitError().solveLong()
    }.fold(0) { a, b -> a + b }
}

private fun parseInput(input: List<String>): List<Machine> {
    return input.chunked(4).mapNotNull { chunk ->
        if (chunk.size < 3) {
            null
        } else {
            parseMachine(chunk)
        }
    }
}

private fun parseMachine(lines: List<String>): Machine {
    return Machine(
        btnA = parseMachineLine(lines[0]),
        btnB = parseMachineLine(lines[1]),
        prize = parseMachineLine(lines[2]),
    )
}

val buttonRegex = """X[+=](?<x>[0-9]*), Y[+=](?<y>[0-9]*)""".trimMargin().toRegex()

private fun parseMachineLine(line: String): VectorLong {
    return buttonRegex.find(line)!!.groups.let { groups ->
        VectorLong(groups["x"]!!.value.toLong(), groups["y"]!!.value.toLong())
    }
}
