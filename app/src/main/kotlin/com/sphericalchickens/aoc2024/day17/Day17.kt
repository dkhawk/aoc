package com.sphericalchickens.aoc2024.day17

import com.sphericalchickens.utils.*



import kotlin.math.pow

val testInput = """
    Register A: 729
    Register B: 0
    Register C: 0

    Program: 0,1,5,4,3,0
""".trimIndent().lines()

val testInput2 = """
    Register A: 117440
    Register B: 0
    Register C: 0
    
    Program: 0,3,5,4,3,0
""".trimIndent().lines()

// Register A: 467631540015530
val testInput3 = """
    Register A: 4676315400155301
    Register B: 0
    Register C: 0
    
    Program: 2,4,1,2,7,5,4,5,1,3,5,5,0,3,3,0
""".trimIndent().lines()

fun main() {
    val input = readLines("inputs/17")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
}

private fun part1(input: List<String>, replacementA: String? = null): String {
    val registers = input.take(3).map { it.substringAfter(": ").trim().toLong() }.toMutableList()
    val program = input[4].substringAfter(": ").split(",").map { it.toInt() }

    val output = mutableListOf<Int>()

    if (replacementA != null) {
        registers[0] = replacementA.reversed().toLong(8)
        "Using ${registers[0]} for A".println()
    }

    val computer = Computer(
        program = program,
        initialRegisters = registers,
        onOutput = { output.add(it) },
    )

    computer.run()

    return output.joinToString(",").also { println(it) }
}

private fun List<Int>.toLong(): Long {
    return fold(0L) { acc, value ->
        (acc shl 3) + value
    }
}

fun expandString(input: String): Sequence<String> {
    return sequence {
        if (input.isEmpty()) {
            yield("")
            return@sequence
        }

        val firstChar = input[0]
        val remainingString = input.substring(1)

        if (firstChar == '[') {
            val closingBracket = input.indexOf(']')
            val options = input.substring(1, closingBracket)
            val afterBracket = input.substring(closingBracket + 1)
            for (option in options) {
                yieldAll(expandString(afterBracket).map { option + it })
            }
        } else {
            yieldAll(expandString(remainingString).map { firstChar + it })
        }
    }
}

fun expandString2(input: String): List<String> {
    val result = mutableListOf<String>()
    val stack = ArrayDeque<Pair<String, Int>>()

    var i = 0
    while (i < input.length) {
        when (input[i]) {
            '[' -> {
                val closingBracket = input.indexOf(']', i)
                val options = input.substring(i + 1, closingBracket)
                stack.addLast(Pair(input.substring(0, i), i))
                for (option in options) {
                    stack.addLast(Pair(stack.last().first + option, closingBracket + 1))
                }
                i = closingBracket + 1
            }
            ']' -> {
                // This should not happen as '[' is handled above
                i++
            }
            else -> {
                if (stack.isEmpty()) {
                    stack.addLast(Pair(input[i].toString(), i + 1))
                } else {
                    while (stack.isNotEmpty() && stack.last().second <= i) {
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty()) {
                        stack.addLast(Pair(stack.last().first + input[i], i + 1))
                    } else {
                        stack.addLast(Pair(input[i].toString(), i + 1))
                    }
                }
                i++
            }
        }
    }

    while (stack.isNotEmpty()) {
        result.add(stack.removeLast().first)
    }

    return result
}


//private fun part2b(input: List<String>): String {
//    val program = input[4].substringAfter(": ").split(",").map { it.toInt() }
//
//    "Program size is: ${program.size}".println()
//    "Program is: ${program}".println()
//
//    // Uses base64
//    val knownCorrect = mutableListOf<Int>()
//
//    fun createGuess(digits: List<Int>): Long {
//        return digits.reversed().fold(0L) { acc, i ->
//            acc shl 6 + i
//        }
//    }
//
//    val candidate = createGuess(knownCorrect)
//
//    var matchSize = 1
//
//    while (matchSize < 4) {
//
//        val num = createGuess(knownCorrect)
//
//        val f = (0..(1 shl 6)).first {
//            val candidate = (num shl 6) + it
//            checkCandidate(candidate, program, matchSize)
//        }
//
//        "$f".println()
//
//        knownCorrect.add(f)
//
//        matchSize += 1
//    }
//
//    return ""
//}

private fun part2(input: List<String>): String {
//    val registers = input.take(3).map { it.substringAfter(": ").trim().toInt() }.toMutableList()
    val program = input[4].substringAfter(": ").split(",").map { it.toInt() }

    "Program size is: ${program.size}".println()
    "Program is: ${program}".println()

    val knownDigits = mutableListOf<Int>() // 7 is the first digit!

    val candidates = ArrayDeque<List<Int>>()

//    repeat(8) { a ->
//        repeat(8) { b ->
//            repeat(8) { c ->
//                repeat(8) { d ->
//                    candidates.add(listOf(a, b, c, d))
//                }
//            }
//        }
//    }

//    candidates.joinToString("\n").println()

//    val bases2 = "467[46][37][17][457][145][015]" // [37][17]"

//    val bases = "467[46]3[17][57][14][05]"

//    val bases = "4676"
//    val bases = "4676315400[123]"
//    val bases = "46763154001"
    val bases = "467631540015530[15]"

//    """
//        467[46]3[17][57][14][05]
//    """.trimIndent()

//    expandString(base).toList().println()
//
//    return ""

    var matchSize = 16

    var histograms = mutableMapOf<Int, MutableList<Int>>()

    fun addToHistograms(histograms: MutableMap<Int, MutableList<Int>>, v: String) {

        v.reversed().forEachIndexed { index, c ->
            val d = c - '0'
            // get the histogram for the index
            val hist = histograms.getOrPut(index) { List(10) { 0 }.toMutableList() }
            hist[d] += 1
        }

//        v.reversed().forEachIndexed { index, c ->
//            val d = c - '0'
//            histograms.getOrPut(index, { List(10) { it }.toMutableList() })[d]+=1
//        }
    }

    repeat(1000000) {
        expandString(bases).forEach { base ->
            val candidate = (it.toLong() shl (base.length * 3)) + base.reversed().toLong(8)

            if (checkCandidate(candidate, program, matchSize)) {
//                candidate.println()
                val v = candidate.toString(8)

                addToHistograms(histograms, v)

//                v.println()
//                println()
            }
        }
    }

//    histograms.keys.sorted().forEach { key ->
//        histograms[key].println()
//    }
//
//    println()

    histograms.keys.sorted().map { key ->
        val h = histograms[key]!!.mapIndexed { index, c -> index to c }.filter { it.second > 0 }

        if (h.size > 1) {
            h.joinToString("", prefix = "[", postfix = "]") { "${it.first}" }
        } else {
            "${h.first().first}"
        }
    }.joinToString("").println()

    return ""


    while (candidates.isNotEmpty()) {
        val candidate = candidates.removeFirst()
        if (checkCandidate(candidate.toLong(), program, matchSize)) {
            candidate.println()
        }
    }

    return ""

    // regA.toList().println()

//    var matchSize = candidates.size + 1

    while (matchSize < 6) {
        val regA = sequence {
            repeat(8) { a ->
                repeat(8) { b ->
                    yield(listOf(a, b))
                }
            }
        }

        val f = regA.filter { list ->
            val digits = list + knownDigits

//            digits.println()

            val A = digits.fold(0L) { acc, value ->
                (acc shl 3) + value
            }

            val registers = listOf(A, 0L, 0L)

            val output = mutableListOf<Int>()

            val computer = Computer(
                program = program,
                initialRegisters = registers,
                onOutput = { output.add(it) },
            )

            while (!computer.stopped()) {
                computer.step()
                if (output.size >= matchSize) {
                    break
                }
            }

            val match = output.take(matchSize).zip(program.take(matchSize)).all { it.first == it.second }
            match
        }.toList()

        f.println()

        knownDigits.println()

        matchSize += 1

        break
    }

//    program.windowed(2, 2) { instruction ->
//        (instruction[0] to instruction[1]).toOperation().println()
//    }

//    // Octal breakdown of register A initial value
//    val regAStart = mutableListOf<Int>(0)
//
//    var regA = sequence<List<Int>> {
//        List(8) { it }.fold(0) {acc, value -> acc + value}
//    }
//
//    var candidate = 0
//
//    while(true) {
//        val aStart = (regAStart.fold(0L) { acc, value ->
//            (acc shl 3) + value
//        } shl 3) + candidate
//
//        val registers = listOf(aStart, 0L, 0L)
//
//        var success = true
//        val output = mutableListOf<Int>()
//        val computer = Computer(
//            program = program,
//            initialRegisters = registers,
//            onOutput = {
//                output.add(it)
//
//                if (output.size > 1) {
//                    if (output[0] == program[0] && output[1] == program[1]) {
//                        "We found one: $candidate!!".println()
//                        error("break")
//                    } else {
//                        success = false
//                    }
//                }
////
////                if (output.last() != program[output.lastIndex]) {
////                    success = false
////                } else {
////                    "We found one!!".println()
////                    regAStart.add(candidate)
////                    error("break")
////                }
//            }
//        )
//
//        while (success && !computer.stopped()) {
//            computer.step()
//        }
//
//        if (!success) {
//            "$regAStart + $aStart failed".println()
//
//            candidate += 1
//
//            if (candidate > 1000) {
//                error("Failed!!")
//            }
//
////            regAStart[regAStart.lastIndex] = regAStart.last() + 1
////
////            if (regAStart.last() >= 8) {
////                regAStart.removeLast()
////                regAStart.add(0)
////                regAStart.add(0)
////            }
//        } else {
//            "$regAStart succeeded!".println()
//            break
//        }
//    }

//    println()
//    registers.joinToString("\n").println()

    return ""
//
//    val output = mutableListOf<Int>()
//
//    registers[0] = 117440
//
//    val computer = Computer(
//        program = program,
//        initialRegisters = registers,
//        onOutput = { output.add(it) },
//    )
//
//    computer.run()
//
//    return output.joinToString(",").also { println(it) }
}

fun checkCandidate(candidate: Long, program: List<Int>, matchSize: Int): Boolean {
    val output = mutableListOf<Int>()

    val computer = Computer(
        program = program,
        initialRegisters = listOf(candidate, 0L, 0L),
        onOutput = { output.add(it) },
    )

    while (!computer.stopped() && output.size < matchSize) {
        computer.step()
    }

    return output.take(matchSize).zip(program.take(matchSize)).all { it.first == it.second }
}

sealed interface Operand {
    fun getValue(computer: Computer): Long
}

data class Combo(val value: Int) : Operand {
    override fun getValue(computer: Computer): Long {
        return when (value) {
            4 -> computer.registers[0]
            5 -> computer.registers[1]
            6 -> computer.registers[2]
            7 -> error("Invalid combo value: $value")
            else -> value.toLong()
        }
    }

    override fun toString(): String {
        return when (value) {
            4 -> "A"
            5 -> "B"
            6 -> "C"
            7 -> "????"
            else -> value.toString()
        }
    }
}

data class Literal(val value: Int) : Operand {
    override fun getValue(computer: Computer): Long {
        return value.toLong()
    }

    override fun toString(): String {
        return "$value"
    }
}


sealed interface Operation {
    operator fun invoke(computer: Computer)
}

data class Adv(val operand: Combo): Operation {
    override fun invoke(computer: Computer) {
        val answer = (computer.registers[0] / ((2.0).pow(operand.getValue(computer).toInt()))).toLong()
        computer.registers[0] = answer
        computer.advance()
    }

    override fun toString(): String {
        return "adv A / (2^$operand) -> A"
    }
}

data class Bxl(val operand: Literal) : Operation {
    override fun invoke(computer: Computer) {
        computer.registers[1] = operand.getValue(computer) xor computer.registers[1]
        computer.advance()
    }

    override fun toString(): String {
        return "bxl $operand xor B -> B"
    }
}

data class Bst(val operand: Combo) : Operation {
    override fun invoke(computer: Computer) {
        computer.registers[1] = operand.getValue(computer) and 7
        computer.advance()
    }

    override fun toString(): String {
        return "bst $operand mod 8 -> B"
    }
}

data class Jnz(val operand: Literal) : Operation {
    override fun invoke(computer: Computer) {
        if (computer.registers[0] == 0L) {
            computer.advance()
        } else {
            computer.programCounter = operand.getValue(computer).toInt()
        }
    }

    override fun toString(): String {
        return "jnz $operand"
    }
}

data class Bxc(val operand: Literal) : Operation {
    override fun invoke(computer: Computer) {
        computer.registers[1] = computer.registers[1] xor computer.registers[2]
        computer.advance()
    }

    override fun toString(): String {
        return "bxc B xor C -> B"
    }
}

data class Out(val operand: Combo) : Operation {
    override fun invoke(computer: Computer) {
        computer.onOutput(operand.getValue(computer).toInt() and 7)
        computer.advance()
    }

    override fun toString(): String {
        return "out $operand mod 8"
    }
}

data class Bdv(val operand: Combo) : Operation {
    override fun invoke(computer: Computer) {
        val answer = (computer.registers[0] / ((2.0).pow(operand.getValue(computer).toInt()))).toLong()
        computer.registers[1] = answer
        computer.advance()
    }

    override fun toString(): String {
        return "bdv A / (2^$operand) -> B"
    }
}

data class Cdv(val operand: Combo) : Operation {
    override fun invoke(computer: Computer) {
        val answer = (computer.registers[0] / ((2.0).pow(operand.getValue(computer).toInt()))).toLong()
        computer.registers[2] = answer
        computer.advance()
    }

    override fun toString(): String {
        return "cdv A / (2^$operand) -> C"
    }
}

fun Pair<Int, Int>.toOperation(): Operation {
    val (opcode, operand) = this

    return when (opcode) {
        0 -> Adv(Combo(operand))
        1 -> Bxl(Literal(operand))
        2 -> Bst(Combo(operand))
        3 -> Jnz(Literal(operand))
        4 -> Bxc(Literal(operand))
        5 -> Out(Combo(operand))
        6 -> Bdv(Combo(operand))
        7 -> Cdv(Combo(operand))
        else -> error("Unknown operation: $opcode")
    }
}

class Computer(
    val program: List<Int>,
    initialRegisters: List<Long>,
    val onOutput: (Int) -> Unit
) {
    val registers = initialRegisters.toMutableList()

    var programCounter = 0

    fun stopped(): Boolean {
        return programCounter > program.lastIndex
    }

    fun run() {
        while (!stopped()) {
            step()
        }
    }

    fun step() {
        val opcode = program[programCounter]
        val operand = program[programCounter + 1]

        val operation = (opcode to operand).toOperation()

        operation(this)
    }

    fun advance() {
        programCounter += 2
    }
}




























