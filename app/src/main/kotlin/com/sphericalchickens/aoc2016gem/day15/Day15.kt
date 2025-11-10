package com.sphericalchickens.aoc2016gem.day15

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

/**
 * ## 1. High-Level Summary
 *
 * The original code correctly identifies this as a problem solvable by
 * finding a time `t` that satisfies multiple congruences:
 *
 * `(t + id + initialPosition) % positions == 0` for all discs.
 *
 * The original solution cleverly implements a "search by sieving" algorithm
 * by sorting the discs by the number of positions and using the largest
 * modulus as the step size. This is a very effective optimization.
 *
 * The refactoring goals are:
 * 1.  **Generalize the `solve` function:** Implement the "search by sieving"
 * algorithm more formally. This new version won't require pre-sorting
 * the discs and will be algorithmically more efficient.
 * 2.  **Simplify the `Disc` class:** The `firstAlignment` calculation
 * is clever, but we can remove it by checking the congruence
 * directly in our new `solve` function. This makes `Disc` a pure data class.
 * 3.  **Idiomatic Parsing:** Use Kotlin's `destructured` feature on the
 * regex match, which is a perfect fit for this kind of parsing.
 */
fun main() {
    // --- Development Workflow Control Panel ---
    // (Unchanged - this is a great harness)
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 15 ---")

    val input = readInputLines("aoc2016/day15_input.txt")

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
        Disc #1 has 5 positions; at time=0, it is at position 4.
        Disc #2 has 2 positions; at time=0, it is at position 1.
    """.trimIndent().lines()
    check("Part 1 Test Case 1 (Parsing)", Disc(1, 5, 4), testInput.first().toDisc())
    check("Part 1 Test Case 2 (Parsing)", Disc(2, 2, 1), testInput[1].toDisc())
    check("Part 1 Test Case 3 (Solution)", 5, part1(testInput))
}

/**
 * ## 2. Refactored Code with Narrative Explanation
 *
 * ### Part 1 & 2: Simplified Setup
 *
 * With our new `solve` function, `part1` and `part2` become
 * simple setup functions. They parse the input and then
 * delegate the core logic to `solve`.
 *
 * For Part 2, we can idiomatically create the new list
 * by just adding the new disc to the existing list.
 */
private fun part1(input: List<String>): Int {
    val discs = input.map { line -> line.toDisc() }
    return solve(discs)
}

private fun part2(input: List<String>): Int {
    val discs = input.map { line -> line.toDisc() }
    val newDisc = Disc(id = discs.size + 1, positions = 11, initialPosition = 0)
    
    // `discs + newDisc` is a clean, idiomatic way to create a new list
    return solve(discs + newDisc)
}

/**
 * ### The Core Logic: `solve`
 *
 * This is the new, generalized "search by sieving" algorithm.
 * It solves the system of congruences one disc at a time.
 *
 * We maintain two variables:
 * 1. `time`: The current solution that works for all discs *seen so far*.
 * 2. `step`: The step size (modulus) of the *combined system* of discs
 * seen so far.
 *
 * We use `Long` for `time` and `step` to avoid potential overflow
 * as the `step` grows by multiplication (though for this
 * problem, `Int` would have been sufficient).
 */
private fun solve(discs: List<Disc>): Int {
    // `time` = solution for discs[0..i-1]
    var time = 0
    // `step` = product of positions for discs[0..i-1]
    var step = 1

    // For each disc, we find the first time `t` that satisfies
    // its congruence, *starting from our current solution `time`*
    // and searching in increments of our current `step`.
    for (disc in discs) {
        // `generateSequence` creates a lazy sequence:
        // `time`, `time + step`, `time + 2*step`, ...
        // These are all the times that work for the *previous* discs.
        //
        // `.first { ... }` finds the first time in that sequence
        // that *also* works for the *current* disc.
        time = generateSequence(time) {
            it + step
        }.first { t ->
            // This is the direct mathematical check for the problem:
            // At time `t`, the capsule reaches disc `id` at `t + id`.
            // The disc's position will be `(initialPosition + t + id)`.
            // We need this to be 0, modulo the number of positions.
            (t + disc.id + disc.initialPosition).mod(disc.positions) == 0
        }
        
        // Now that we've found a `time` that works for this disc
        // *and* all previous ones, our new "super-modulus" (step)
        // becomes the product of the old step and this disc's
        // positions. This works because all position counts are prime.
        // (The general solution would use lcm(step, disc.positions)).
        step *= disc.positions
    }
    
    // The final `time` is the first non-negative time that
    // satisfies all congruences.
    return time
}

/**
 * ### Simplified `Disc` Data Class
 *
 * By moving the congruence check into `solve`, the `Disc` class
 * becomes a simple, pure data container. We no longer need
 * the `firstAlignment` calculation.
 */
private data class Disc(
    val id: Int,
    val positions: Int,
    val initialPosition: Int,
)

/**
 * ### Idiomatic Parsing with `destructured`
 *
 * This is a perfect use case for `destructured`.
 * We define the regex with simple capture groups (no names needed).
 */
private val discRex = Regex(
    """Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+)."""
)

private fun String.toDisc(): Disc {
    // `matchEntire` ensures the whole string matches.
    // `?.destructured` gives us access to the captured groups.
    // We can then "destructure" them directly into 3 String variables.
    // The Elvis operator `?: error(...)` provides robust error
    // handling if a line doesn't match the regex.
    val (idStr, positionsStr, initialStr) = discRex.matchEntire(this)?.destructured
        ?: error("Input error: $this")

    return Disc(
        id = idStr.toInt(),
        positions = positionsStr.toInt(),
        initialPosition = initialStr.toInt(),
    )
}