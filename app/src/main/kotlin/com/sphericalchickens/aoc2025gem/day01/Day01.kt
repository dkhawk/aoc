package com.sphericalchickens.aoc2025gem.day01

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs
import kotlin.system.measureTimeMillis

// We define our domain constants at the top level for easy configuration and readability.
private const val DIAL_SIZE = 100
private const val START_POSITION = 50

fun main() {
    println("--- Advent of Code 2025, Day 1 (Gemini Refactor) ---")
    
    // In a real scenario, this file path would be relative to the project root.
    // Ensure the input file exists at this location.
    val input = readInputLines("aoc2025/day01_input.txt")

    runTests()
    solve(input)
}

private fun runTests() {
    println("🧪 Running Tests...")
    
    val testInput = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()

    // Parse once to verify our parsing logic during tests
    val instructions = testInput.map { Instruction.parse(it) }

    // Part 1 Check
    check("Part 1 Test Case", 3, part1(instructions))

    // Part 2 Check
    check("Part 2 Test Case", 6, part2(instructions))
    
    // Detailed Part 2 State Logic Check (from your original code)
    // We recreate this to ensure our refactored logic maintains the exact behavior required.
    val detailedTestInput = """
        L68 -> 82, 1
        L30 -> 52, 1
        R48 -> 0, 2
        L5 -> 95, 2
        R60 -> 55, 3
        L55 -> 0, 4
        L1 -> 99, 4
        L99 -> 0, 5
        R14 -> 14, 5
        L82 -> 32, 6
    """.trimIndent().lines()

    var currentState = DialState(START_POSITION)
    detailedTestInput.forEach { line ->
        val parts = line.split("->")
        val inputStr = parts[0].trim()
        val (expectedPos, expectedTotal) = parts[1].split(",").map { it.trim().toInt() }
        
        val instruction = Instruction.parse(inputStr)
        val nextState = currentState.applyInstruction(instruction)

        check(
            "State after $inputStr", 
            DialState(expectedPos, expectedTotal), 
            nextState
        )
        currentState = nextState
    }
    
    println("✅ All tests passed!")
}

private fun solve(input: List<String>) {
    // Parse the input immediately. This fails fast if data is malformed and 
    // keeps the solver functions clean.
    val instructions = input.map { Instruction.parse(it) }

    println("🎁 Solving Part 1...")
    val time1 = measureTimeMillis {
        println("   Part 1: ${part1(instructions)}")
    }
    println("   (Runtime: ${time1}ms)")

    println("🎀 Solving Part 2...")
    val time2 = measureTimeMillis {
        println("   Part 2: ${part2(instructions)}")
    }
    println("   (Runtime: ${time2}ms)")
}

/**
 * Encapsulates a single instruction.
 * Using a data class here makes the code self-documenting.
 */
private data class Instruction(val direction: Direction, val amount: Int) {
    // We compute the net movement once. 'L' is negative, 'R' is positive.
    val movement: Int = if (direction == Direction.LEFT) -amount else amount

    enum class Direction { LEFT, RIGHT }

    companion object {
        // A simple regex to separate the letter from the number.
        // destructuring declaration ((d, a)) makes extraction elegant.
        fun parse(input: String): Instruction {
            val directionChar = input.first()
            val amount = input.drop(1).toInt()
            
            return Instruction(
                direction = when (directionChar) {
                    'L' -> Direction.LEFT
                    'R' -> Direction.RIGHT
                    else -> error("Unknown direction: $directionChar")
                },
                amount = amount
            )
        }
    }
}

/**
 * Represents the state of the dial.
 * By making this a data class, we get `equals` and `hashCode` for free, simplifying tests.
 * We default `zeroCrossings` to 0 for the initial state.
 */
private data class DialState(val position: Int, val zeroCrossings: Int = 0) {
    
    /**
     * Calculates the next state based on an instruction.
     * This encapsulates the core domain logic for Part 2.
     */
    fun applyInstruction(instruction: Instruction): DialState {
        val nextPosition = (position + instruction.movement).posMod(DIAL_SIZE)
        
        // Count full rotations (e.g., a move of 205 on a dial of 100 crosses zero twice guaranteed)
        val fullRotations = abs(instruction.movement) / DIAL_SIZE
        
        // Determine if we crossed zero during the "partial" remainder of the movement.
        val crossedZeroInRemainder = didCrossZero(instruction.movement, nextPosition)
        
        return DialState(
            position = nextPosition,
            zeroCrossings = zeroCrossings + fullRotations + (if (crossedZeroInRemainder) 1 else 0)
        )
    }

    /**
     * Helper to determine if the transition from [position] to [nextPosition] crossed or landed on 0.
     */
    private fun didCrossZero(movement: Int, nextPosition: Int): Boolean {
        return if (movement > 0) {
            // Moving Right (Positive)
            // We crossed 0 if the new position wraps around to be smaller than the start,
            // OR if we landed exactly on 0 (which conceptually is 100 in the wrap logic).
            nextPosition < position || nextPosition == 0
        } else {
            // Moving Left (Negative)
            // We crossed 0 if the new position wraps around to be larger than the start.
            // Special Case: If we started at 0 and moved left, we immediately leave 0, 
            // which does NOT count as a crossing based on the problem rules.
            position != 0 && (nextPosition > position || nextPosition == 0)
        }
    }
}

/**
 * Part 1: We only care about how many times the dial *ends up* at 0.
 * We can verify this purely by looking at the final positions.
 */
private fun part1(instructions: List<Instruction>): Int {
    // fold allows us to accumulate state over the collection.
    // We only track the current position here, but we need to count how many times
    // that position ends up being 0.
    // Since 'fold' returns the final accumulator, we actually need 'runningFold' 
    // to see the intermediate states, or just a custom fold that counts.
    
    // Efficient approach: Track (Position, Count).
    data class P1State(val pos: Int, val count: Int)
    
    val finalState = instructions.fold(P1State(START_POSITION, 0)) { state, instr ->
        val nextPos = (state.pos + instr.movement).posMod(DIAL_SIZE)
        val hitZero = if (nextPos == 0) 1 else 0
        P1State(nextPos, state.count + hitZero)
    }
    
    return finalState.count
}

/**
 * Part 2: We track the cumulative zero crossings using our [DialState] logic.
 */
private fun part2(instructions: List<Instruction>): Int {
    val finalState = instructions.fold(DialState(START_POSITION)) { state, instr ->
        state.applyInstruction(instr)
    }
    return finalState.zeroCrossings
}

/**
 * Idiomatic helper: Positive Modulo.
 * Kotlin's `%` operator is "remainder", so `-5 % 100` is `-5`.
 * In modular arithmetic (dial logic), we want `-5` to wrap to `95`.
 */
private infix fun Int.posMod(mod: Int): Int {
    val r = this % mod
    return if (r < 0) r + mod else r
}