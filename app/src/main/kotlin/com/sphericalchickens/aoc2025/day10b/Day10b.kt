package com.sphericalchickens.aoc2025.day10b

import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import java.util.ArrayDeque
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 10 (Day10b: MRV + Propagation Search) ---")

    val input = readInputLines("aoc2025/day10_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!\n")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
            part1(input)
        }
        println("   Part 1: $part1Result")
        println("Part 1 runtime: ${formatDuration(part1Duration)}\n")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!\n")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            part2(input)
        }
        println("   Part 2 Total (for solved machines): $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}\n")
    }
}

// --- Test Implementations ---

private fun runPart1Tests() {
    val sampleInput = listOf(
        "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
        "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
        "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"
    )
    val result = part1(sampleInput)
    check(result == 7) { "Part 1 sample failed: Expected 7, got $result" }
}

private fun runPart2Tests() {
    val sampleInput = listOf(
        "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
        "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
        "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"
    )

    val answer = sampleInput.mapIndexed { idx, line ->
        println("\n--- Part 2 Test Problem #${idx + 1} ---")
        val (buttons, targets) = parseLine(line)
        solveMachineMRV(buttons, targets, timeoutMs = 2000L, debug = true)
    }.sum()

    check(answer == 33L) { "Part 2 sample failed: Expected 33, got $answer" }
}

// --- Part 1 Implementation ---

private fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        val (targetMask, buttons) = parsePart1Line(line)
        solveMachinePart1(targetMask, buttons)
    }
}

private fun parsePart1Line(line: String): Pair<Int, List<Int>> {
    val targetStr = Regex("""\[([.#]+)\]""").find(line)!!.groupValues[1]
    var targetMask = 0
    targetStr.forEachIndexed { idx, ch ->
        if (ch == '#') {
            targetMask = targetMask or (1 shl idx)
        }
    }

    val buttons = Regex("""\(([^)]+)\)""").findAll(line).map { m ->
        val indices = m.groupValues[1].split(",").map { it.trim().toInt() }
        var mask = 0
        indices.forEach { idx -> mask = mask or (1 shl idx) }
        mask
    }.toList()

    return targetMask to buttons
}

private fun solveMachinePart1(targetMask: Int, buttons: List<Int>): Int {
    if (targetMask == 0) return 0

    val visited = mutableMapOf<Int, Int>()
    val queue = ArrayDeque<Int>()

    visited[0] = 0
    queue.add(0)

    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        val dist = visited[curr]!!

        if (curr == targetMask) return dist

        for (buttonMask in buttons) {
            val next = curr xor buttonMask
            if (next !in visited) {
                visited[next] = dist + 1
                queue.add(next)
            }
        }
    }

    error("No solution found for Part 1 line")
}

// --- Part 2 Implementation ---

private fun part2(input: List<String>): Long {
    var totalPresses = 0L
    var timedOutCount = 0

    input.forEachIndexed { index, line ->
        val (buttons, targets) = parseLine(line)
        val result = solveMachineMRV(buttons, targets, timeoutMs = 2000L, debug = false)
        if (result == -1L) {
            println("⚠️ [Timeout > 2s] Aborted calculation on line #${index + 1}:\n   $line")
            timedOutCount++
        } else {
            totalPresses += result
        }
    }

    if (timedOutCount > 0) {
        println("\n⚠️ Total timed out machines: $timedOutCount out of ${input.size}")
    }

    return totalPresses
}

fun parseLine(line: String): Pair<List<List<Int>>, List<Long>> {
    val buttons = Regex("""\(([^)]+)\)""").findAll(line).map { m ->
        m.groupValues[1].split(",").map { it.trim().toInt() }
    }.toList()

    val targets = Regex("""\{([^}]+)\}""").find(line)!!
        .groupValues[1].split(",").map { it.trim().toLong() }

    return buttons to targets
}

/**
 * Solves Part 2 using Minimum Remaining Values (MRV) counter targeting
 * combined with subgroup elimination constraint propagation.
 *
 * Aborts and returns -1L if search execution exceeds [timeoutMs].
 */
fun solveMachineMRV(
    buttons: List<List<Int>>,
    targets: List<Long>,
    timeoutMs: Long = 2000L,
    debug: Boolean = false
): Long {
    val numCounters = targets.size
    val numButtons = buttons.size

    val buttonToCounters = buttons.map { it.toSet() }

    var bestMin = Long.MAX_VALUE
    var statesVisited = 0L
    val startTime = System.currentTimeMillis()
    var timedOut = false

    fun dfs(currentCounts: LongArray, currentPresses: Long, activeButtons: List<Int>) {
        // Check timeout condition periodically (every 1024 states for low overhead)
        if (statesVisited and 1023L == 0L) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                timedOut = true
                return
            }
        }

        statesVisited++

        val remaining = LongArray(numCounters) { i -> targets[i] - currentCounts[i] }

        // 1. Check for overshot targets
        if (remaining.any { it < 0L }) return

        // 2. Base case: All targets satisfied exactly
        if (remaining.all { it == 0L }) {
            if (currentPresses < bestMin) {
                bestMin = currentPresses
                if (debug) {
                    println("  🎯 Found feasible solution: $bestMin presses (States visited: $statesVisited)")
                }
            }
            return
        }

        // Prune if current presses already match or exceed best solution found
        if (currentPresses >= bestMin) return

        // 3. Subgroup Elimination: Disable buttons touching ANY counter that is already 0
        val validButtons = activeButtons.filter { bIdx ->
            buttonToCounters[bIdx].none { c -> remaining[c] == 0L }
        }

        // 4. Dead-end check: Any unsatisfied counter that has NO valid buttons left
        for (c in 0 until numCounters) {
            if (remaining[c] > 0L) {
                val canService = validButtons.any { bIdx -> c in buttonToCounters[bIdx] }
                if (!canService) return // Prune dead end
            }
        }

        // 5. MRV Heuristic: Find unsatisfied counter with the LOWEST remaining target
        val mostConstrainedCounter = (0 until numCounters)
            .filter { remaining[it] > 0L }
            .minByOrNull { remaining[it] } ?: return

        val candidateButtons = validButtons.filter { bIdx ->
            mostConstrainedCounter in buttonToCounters[bIdx]
        }

        if (candidateButtons.isEmpty()) return

        val bIdx = candidateButtons.first()
        val btnCounters = buttonToCounters[bIdx]

        val maxPresses = btnCounters.minOf { c -> remaining[c] }

        // Branch 1: Try pressing button from maxPresses down to 1
        for (k in maxPresses downTo 1L) {
            if (timedOut) return

            val nextCounts = currentCounts.clone()
            for (c in btnCounters) {
                nextCounts[c] += k
            }
            dfs(nextCounts, currentPresses + k, validButtons)
        }

        // Branch 2: Do NOT press this button at all (disable it for remaining branch)
        if (!timedOut) {
            val nextMask = validButtons.filter { it != bIdx }
            dfs(currentCounts, currentPresses, nextMask)
        }
    }

    val initialActiveButtons = (0 until numButtons).toList()
    dfs(LongArray(numCounters), 0L, initialActiveButtons)

    if (timedOut) {
        if (debug) {
            println("⏱️ [Timeout] Solver exceeded ${timeoutMs}ms after $statesVisited states.")
        }
        return -1L
    }

    if (debug) {
        println("🔍 MRV Solver finished with min $bestMin presses after $statesVisited states.")
    }

    return if (bestMin == Long.MAX_VALUE) -1L else bestMin
}