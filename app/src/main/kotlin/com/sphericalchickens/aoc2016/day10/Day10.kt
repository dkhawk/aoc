package com.sphericalchickens.aoc2016.day10

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 10 ---")

    val input = readInputLines("aoc2016/day10_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input, 61, 17)
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
        value 5 goes to bot 2
        bot 2 gives low to bot 1 and high to bot 0
        value 3 goes to bot 1
        bot 1 gives low to output 1 and high to bot 0
        bot 0 gives low to output 2 and high to output 0
        value 2 goes to bot 2
    """.trimIndent().lines()

    check("Part 1 Test Case 1", 2, part1(testInput, 5, 2))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun solve(input: List<String>): Pair<MutableMap<Int, Robot>, MutableMap<Int, OutputBin>> {
    val bots = mutableMapOf<Int, Robot>()
    val bins = mutableMapOf<Int, OutputBin>()

    val valueInstructions = mutableListOf<ValueInstruction>()
    val exchangeInstructions = mutableListOf<ExchangeInstruction>()

    input.forEach { line ->
        if (line.startsWith("value")) {
            valueInstructions.add(line.parseValue())
        } else {
            exchangeInstructions.add(line.parseExchange())
        }
    }

    valueInstructions.forEach { instruction ->
        instruction.token
        bots.getOrPut(instruction.botId) {
            Robot(instruction.botId)
        }.giveToken(instruction.token)
    }

    while (exchangeInstructions.isNotEmpty()) {
        val toRemove = mutableListOf<ExchangeInstruction>()

        exchangeInstructions.forEach { instruction ->
            // Do we know which tokens the source bot has?
            val sourceBot = bots.getOrPut(instruction.sourceBot) {
                Robot(instruction.sourceBot)
            }

            if (sourceBot.tokens.size == 2) {
                val lowTokenId = sourceBot.tokens.first()
                val highTokenId = sourceBot.tokens.last()

                var lowTarget = instruction.lowTarget
                var highTarget = instruction.highTarget

                lowTarget = if (lowTarget is Robot) {
                    bots.getOrPut(lowTarget.id) { Robot(lowTarget.id) }
                } else {
                    bins.getOrPut(lowTarget.id) { OutputBin(lowTarget.id) }
                }

                highTarget = if (highTarget is Robot) {
                    bots.getOrPut(highTarget.id) { Robot(highTarget.id) }
                } else {
                    bins.getOrPut(highTarget.id) { OutputBin(highTarget.id) }
                }

                lowTarget.giveToken(lowTokenId)
                highTarget.giveToken(highTokenId)

                toRemove.add(instruction)
            }
        }

        exchangeInstructions.removeAll(toRemove)
    }

    return Pair(bots, bins)
}

// Find the bot responsible for comparing id1 to id2.
private fun part1(input: List<String>, id1: Int, id2: Int): Int {
    val (bots, bins) = solve(input)

    return bots.values.first { bot ->
        bot.tokens.contains(id1) && bot.tokens.contains(id2)
    }.id
}

private val ValueRegex = Regex("""value (\d+) goes to bot (\d+)""")

private fun String.parseValue(): ValueInstruction {
    val (token, botId) = ValueRegex.matchEntire(this)!!.groupValues.drop(1).map { it.toInt() }
    return ValueInstruction(token = token, botId = botId)
}

private val ExchangeRegex = Regex("""bot (\d+) gives low to (\w+) (\d+) and high to (\w+) (\d+)""")

private fun String.parseExchange(): ExchangeInstruction {
    val groups = ExchangeRegex.matchEntire(this)!!.groupValues.drop(1)

    val sourceId = groups[0].toInt()
    val lowType = groups[1]
    val lowId = groups[2].toInt()
    val highType = groups[3]
    val highId = groups[4].toInt()

    val lowTarget = createTarget(lowType, lowId)
    val highTarget = createTarget(highType, highId)

    return ExchangeInstruction(sourceId, lowTarget, highTarget)
}

private fun createTarget(type: String, id: Int) : Target {
    return when (type) {
        "output" -> OutputBin(id)
        "bot" -> Robot(id)
        else -> error("Invalid type: $type")
    }
}

private sealed interface Target {
    val id: Int
    fun giveToken(value: Int)
}

private class OutputBin(override val id: Int) : Target {
    val tokens = mutableSetOf<Int>()
    override fun giveToken(value: Int) {
        tokens.add(value)
    }
}

private class Robot(override val id: Int) : Target {
    val tokens = sortedSetOf<Int>()

    override fun giveToken(value: Int) {
        tokens.add(value)
        if (tokens.size > 2) error("Did not expect more than two tokens")
    }
}

private sealed interface Instruction

private data class ValueInstruction(val token: Int, val botId: Int) : Instruction

private data class ExchangeInstruction(val sourceBot: Int, val lowTarget: Target, val highTarget: Target) : Instruction

private fun part2(input: List<String>): Int {
    val (bots, bins) = solve(input)

    return bins[0]!!.tokens.first() * bins[1]!!.tokens.first() * bins[2]!!.tokens.first()
}
