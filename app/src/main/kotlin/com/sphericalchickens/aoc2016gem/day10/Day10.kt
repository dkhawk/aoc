package com.sphericalchickens.aoc2016gem.day10

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

// The main function and test runners are kept from your original code,
// as they provide a great structure for running the solution.
fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = false // Original was false, no tests provided
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
            // Note: The puzzle prompt hardcodes these values for part 1.
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

    // Test with the values from the example
    check("Part 1 Test Case 1", 2, part1(testInput, 5, 2))
}

// No test case was provided in the original, so we'll leave this empty.
private fun runPart2Tests() {
    // val testInput = """
    // """.trimIndent().lines()
    // check("Part 2 Test Case 1", "expected", part2(testInput))
}

/**
 * Represents a destination for a chip. This is implemented by
 * the stateful [Robot] and [OutputBin] classes.
 */
private sealed interface Target {
    val id: Int
    fun giveToken(value: Int)
}

/**
 * A stateful simulation object. It's a [class] (not a data class)
 * because its [tokens] set is mutable.
 */
private class OutputBin(override val id: Int) : Target {
    val tokens = mutableSetOf<Int>()
    override fun giveToken(value: Int) {
        tokens.add(value)
    }
}

/**
 * A stateful simulation object. We keep your smart use of [sortedSetOf]
 * to manage low/high values.
 */
private class Robot(override val id: Int) : Target {
    // This is the core state of the Robot
    private val tokens = sortedSetOf<Int>()

    /**
     * A read-only view of the tokens this bot is holding.
     * Useful for the part1 check.
     */
    val heldValues: Set<Int> get() = tokens.toSet()

    override fun giveToken(value: Int) {
        tokens.add(value)
        if (tokens.size > 2) {
            error("Bot $id received a third token, which should not happen.")
        }
    }

    /** Checks if the bot is ready to execute its instruction. */
    fun isReady(): Boolean = tokens.size == 2

    /**
     * Gets the low value. Based on the original code's logic,
     * bots *keep* their tokens after comparison, so we don't remove them.
     */
    fun getLow(): Int = tokens.first()

    /** Gets the high value. */
    fun getHigh(): Int = tokens.last()
}

/**
 * A descriptor of a target, parsed from an instruction.
 * This is an immutable data-holder. It knows how to find or
 * create the *actual* [Target] from the simulation state maps.
 */
private sealed class TargetDescriptor {
    /**
     * Resolves this descriptor into a concrete, stateful [Target] object,
     * creating it if it doesn't exist in the maps.
     */
    abstract fun getTarget(
        bots: MutableMap<Int, Robot>,
        bins: MutableMap<Int, OutputBin>
    ): Target

    data class BotTarget(val id: Int) : TargetDescriptor() {
        override fun getTarget(
            bots: MutableMap<Int, Robot>,
            bins: MutableMap<Int, OutputBin>
        ): Target {
            // getOrPut is perfect here: it gets the existing bot
            // or creates/adds/returns a new one atomically.
            return bots.getOrPut(id) { Robot(id) }
        }
    }

    data class OutputTarget(val id: Int) : TargetDescriptor() {
        override fun getTarget(
            bots: MutableMap<Int, Robot>,
            bins: MutableMap<Int, OutputBin>
        ): Target {
            return bins.getOrPut(id) { OutputBin(id) }
        }
    }
}

/**
 * Our instruction models, now simplified.
 * [ValueInstruction] is unchanged.
 * [ExchangeInstruction] now uses the clean [TargetDescriptor].
 */
private sealed interface Instruction
private data class ValueInstruction(val token: Int, val botId: Int) : Instruction
private data class ExchangeInstruction(
    val sourceBotId: Int,
    val lowTarget: TargetDescriptor,
    val highTarget: TargetDescriptor
) : Instruction

private val ValueRegex = Regex("""value (\d+) goes to bot (\d+)""")
private val ExchangeRegex = Regex("""bot (\d+) gives low to (\w+) (\d+) and high to (\w+) (\d+)""")

/**
 * Parses a "value..." line into a [ValueInstruction].
 * Using destructuring on the regex result is a nice, clean Kotlin feature.
 */
private fun String.parseValue(): ValueInstruction {
    val (token, botId) = ValueRegex.matchEntire(this)!!
        .groupValues.drop(1).map { it.toInt() }
    return ValueInstruction(token, botId)
}

/**
 * Parses a "bot..." line into an [ExchangeInstruction].
 */
private fun String.parseExchange(): ExchangeInstruction {
    val groups = ExchangeRegex.matchEntire(this)!!.groupValues.drop(1)

    val sourceId = groups[0].toInt()
    val lowType = groups[1]
    val lowId = groups[2].toInt()
    val highType = groups[3]
    val highId = groups[4].toInt()

    // Create the lightweight descriptors instead of full objects
    val lowTarget = createTargetDescriptor(lowType, lowId)
    val highTarget = createTargetDescriptor(highType, highId)

    return ExchangeInstruction(sourceId, lowTarget, highTarget)
}

/**
 * Factory function to create the correct [TargetDescriptor] subclass.
 */
private fun createTargetDescriptor(type: String, id: Int): TargetDescriptor {
    return when (type) {
        "output" -> TargetDescriptor.OutputTarget(id)
        "bot" -> TargetDescriptor.BotTarget(id)
        else -> error("Invalid target type: $type")
    }
}

/**
 * Runs the full chip simulation.
 * Returns the final state of all bots and output bins.
 */
private fun solve(input: List<String>): Pair<Map<Int, Robot>, Map<Int, OutputBin>> {

    // --- 1. Initialization ---
    // These maps will hold the state of our simulation.
    val bots = mutableMapOf<Int, Robot>()
    val bins = mutableMapOf<Int, OutputBin>()

    // This queue will hold bots that are ready (have 2 tokens)
    // and whose instruction has not yet been processed.
    // ArrayDeque is the standard, efficient queue implementation in Kotlin.
    val readyQueue = ArrayDeque<Robot>()

    // --- 2. Parse Instructions ---
    // We can parse all instructions in one pass using `partition`.
    // This is more idiomatic than iterating and using `if/else`.
    val (valueLines, exchangeLines) = input
        .filter { it.isNotBlank() } // Avoid empty lines
        .partition { it.startsWith("value") }

    val valueInstructions = valueLines.map { it.parseValue() }

    // We map exchange instructions by their source bot ID for O(1) lookup.
    // This is crucial for the queue-based approach.
    // We make it mutable so we can "consume" a rule once it's processed.
    val exchangeRules = exchangeLines.map { it.parseExchange() }
        .associateBy { it.sourceBotId }
        .toMutableMap()

    // --- 3. Priming the Simulation ---
    // Process all initial "value" instructions. This gives bots their
    // starting chips and populates the initial set of ready bots.
    valueInstructions.forEach { instruction ->
        val bot = bots.getOrPut(instruction.botId) { Robot(instruction.botId) }
        bot.giveToken(instruction.token)

        // If this token was the bot's second one, it's ready!
        if (bot.isReady()) {
            readyQueue.add(bot)
        }
    }

    // --- 4. Run the Simulation ---
    // As long as there are ready bots in the queue, we have work to do.
    while (readyQueue.isNotEmpty()) {
        val currentBot = readyQueue.removeFirst()

        // Get the rule for this bot.
        // We *remove* the rule to ensure we only process a bot's
        // instruction *once*. This prevents infinite loops.
        // If the rule is null, this bot was already processed.
        val rule = exchangeRules.remove(currentBot.id) ?: continue

        // Get the tokens (bots don't "lose" them)
        val lowToken = currentBot.getLow()
        val highToken = currentBot.getHigh()

        // --- Process Low Target ---
        val lowTarget = rule.lowTarget.getTarget(bots, bins)
        lowTarget.giveToken(lowToken)
        // If the target was a bot and is *now* ready, add it to the queue.
        if (lowTarget is Robot && lowTarget.isReady()) {
            readyQueue.add(lowTarget)
        }

        // --- Process High Target ---
        val highTarget = rule.highTarget.getTarget(bots, bins)
        highTarget.giveToken(highToken)
        // If this target was a bot and is *now* ready, add it to the queue.
        if (highTarget is Robot && highTarget.isReady()) {
            readyQueue.add(highTarget)
        }
    }

    // The simulation is complete. Return the final state.
    return Pair(bots, bins)
}

/**
 * Finds the ID of the bot responsible for comparing two specific chip values.
 */
private fun part1(input: List<String>, id1: Int, id2: Int): Int {
    val (bots, _) = solve(input)
    val targetSet = setOf(id1, id2)

    // We find the first bot whose final state includes *exactly*
    // the two tokens we care about.
    return bots.values.first { it.heldValues == targetSet }.id
}

/**
 * Finds the product of the chips in output bins 0, 1, and 2.
 */
private fun part2(input: List<String>): Int {
    val (_, bins) = solve(input)

    // Your original use of `!!` is fine here, as the puzzle
    // implies these bins will have values. We'll trust the simulation.
    val v0 = bins[0]!!.tokens.first()
    val v1 = bins[1]!!.tokens.first()
    val v2 = bins[2]!!.tokens.first()

    return v0 * v1 * v2
}