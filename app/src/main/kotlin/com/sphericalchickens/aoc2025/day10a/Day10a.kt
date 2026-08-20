package com.sphericalchickens.aoc2025.day10a

import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import java.util.ArrayDeque
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 10 ---")

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
        println("   Part 2: $part2Result")
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
        solveMachineLinear(buttons, targets, debugRref = true)
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
    return input.sumOf { line ->
        val (buttons, targets) = parseLine(line)
        solveMachineLinear(buttons, targets, debugRref = false)
    }
}

data class Fraction(val num: Long, val den: Long = 1L) : Comparable<Fraction> {
    init { require(den != 0L) }

    val reduced: Fraction by lazy {
        val g = gcd(kotlin.math.abs(num), kotlin.math.abs(den))
        val sign = if (den < 0) -1 else 1
        Fraction((num / g) * sign, (den / g) * sign)
    }

    operator fun plus(other: Fraction) =
        Fraction(num * other.den + other.num * den, den * other.den).reduced

    operator fun minus(other: Fraction) =
        Fraction(num * other.den - other.num * den, den * other.den).reduced

    operator fun times(other: Fraction) =
        Fraction(num * other.num, den * other.den).reduced

    operator fun div(other: Fraction) =
        Fraction(num * other.den, den * other.num).reduced

    override fun compareTo(other: Fraction): Int {
        val diff = this.num * other.den - other.num * this.den
        return diff.compareTo(0L)
    }

    fun isZero() = num == 0L
    fun isInteger() = reduced.den == 1L
    fun toLong() = reduced.num

    override fun toString(): String {
        val r = reduced
        return if (r.den == 1L) "${r.num}" else "${r.num}/${r.den}"
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
}

fun parseLine(line: String): Pair<List<List<Int>>, List<Long>> {
    val buttons = Regex("""\(([^)]+)\)""").findAll(line).map { m ->
        m.groupValues[1].split(",").map { it.trim().toInt() }
    }.toList()

    val targets = Regex("""\{([^}]+)\}""").find(line)!!
        .groupValues[1].split(",").map { it.trim().toLong() }

    return buttons to targets
}

fun printRrefMatrix(mat: Array<Array<Fraction>>, m: Int, n: Int) {
    val colWidths = IntArray(n + 1)
    for (j in 0..n) {
        var maxW = 0
        for (i in 0 until m) {
            maxW = maxOf(maxW, mat[i][j].toString().length)
        }
        colWidths[j] = maxW
    }

    println("┌" + "─".repeat(colWidths.sum() + (n * 3) + 5) + "┐")
    for (i in 0 until m) {
        val rowStr = StringBuilder("│ ")
        for (j in 0 until n) {
            rowStr.append(mat[i][j].toString().padStart(colWidths[j])).append("  ")
        }
        rowStr.append("│ ").append(mat[i][n].toString().padStart(colWidths[n])).append(" │")
        println(rowStr.toString())
    }
    println("└" + "─".repeat(colWidths.sum() + (n * 3) + 5) + "┘")
}

fun solveMachineLinear(buttons: List<List<Int>>, targets: List<Long>, debugRref: Boolean = false): Long {
    val m = targets.size
    val n = buttons.size

    // Augmented matrix [A | b] using exact rational arithmetic
    val mat = Array(m) { i ->
        Array(n + 1) { j ->
            if (j < n) {
                if (buttons[j].contains(i)) Fraction(1) else Fraction(0)
            } else {
                Fraction(targets[i])
            }
        }
    }

    // Row Echelon Form (Gauss-Jordan elimination)
    val pivotCols = mutableListOf<Int>()
    var pivotRow = 0

    for (col in 0 until n) {
        var sel = -1
        for (row in pivotRow until m) {
            if (!mat[row][col].isZero()) {
                sel = row
                break
            }
        }
        if (sel == -1) continue

        // Swap pivot row
        val temp = mat[pivotRow]
        mat[pivotRow] = mat[sel]
        mat[sel] = temp

        // Scale pivot row to 1
        val pivotVal = mat[pivotRow][col]
        for (j in col..n) {
            mat[pivotRow][j] = mat[pivotRow][j] / pivotVal
        }

        // Eliminate column entries in all other rows
        for (r in 0 until m) {
            if (r != pivotRow && !mat[r][col].isZero()) {
                val factor = mat[r][col]
                for (j in col..n) {
                    mat[r][j] = mat[r][j] - factor * mat[pivotRow][j]
                }
            }
        }

        pivotCols.add(col)
        pivotRow++
    }

    val freeCols = (0 until n).filter { it !in pivotCols }

    if (debugRref) {
        println("📊 Reduced Row Echelon Form Matrix [A | b]:")
        printRrefMatrix(mat, m, n)
        println("🔍 Search Dimension: $n variables total → ${pivotCols.size} pivots ($pivotCols), ${freeCols.size} free variables ($freeCols)")
    }

    // Check consistency of 0 = nonzero rows
    for (r in pivotRow until m) {
        if (!mat[r][n].isZero()) return -1L // No solution possible
    }

    var minSum = Long.MAX_VALUE

    // Search through free variable space
    fun searchFree(freeIdx: Int, freeValues: LongArray) {
        if (freeIdx == freeCols.size) {
            // Compute pivot variable values
            val x = LongArray(n)
            for (i in freeCols.indices) {
                x[freeCols[i]] = freeValues[i]
            }

            var sum = freeValues.sum()

            for (r in pivotCols.indices) {
                val pCol = pivotCols[r]
                var valP = mat[r][n]
                for (i in freeCols.indices) {
                    val fCol = freeCols[i]
                    valP -= mat[r][fCol] * Fraction(freeValues[i])
                }

                val pValRed = valP.reduced
                if (!pValRed.isInteger() || pValRed.num < 0) return

                x[pCol] = pValRed.num
                sum += x[pCol]
            }

            minSum = min(minSum, sum)
            return
        }

        val fCol = freeCols[freeIdx]

        // Bounded upper limit based on targets
        var maxLimit = Long.MAX_VALUE
        for (c in buttons[fCol]) {
            maxLimit = min(maxLimit, targets[c])
        }

        for (v in 0..maxLimit) {
            freeValues[freeIdx] = v
            searchFree(freeIdx + 1, freeValues)
        }
    }

    searchFree(0, LongArray(freeCols.size))

    return if (minSum == Long.MAX_VALUE) -1L else minSum
}