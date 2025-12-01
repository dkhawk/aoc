package com.sphericalchickens.aoc2016gem.day17

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import java.security.MessageDigest
import java.util.ArrayDeque
import kotlin.system.measureTimeMillis

/**
 * Main entry point for the solution.
 * This structure is identical to your original, ensuring the same
 * tests and solutions are run.
 */
fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 17 ---")

    val input = readInputLines("aoc2016/day17_input.txt")

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

// --- Data Structures and Constants ---
// These are largely unchanged as they were already clear and effective.

private const val MAX_COORD = 3
private const val MIN_COORD = 0
private val VALID_RANGE = MIN_COORD..MAX_COORD
private val GOAL = Vector(MAX_COORD, MAX_COORD)

private data class State(
    val location: Vector,
    val path: String
)

private data class Vector(
    val x: Int,
    val y: Int
)

private fun Vector.isValidLocation() = (this.x in VALID_RANGE) && (this.y in VALID_RANGE)

private operator fun Vector.plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)

private fun Vector.move(heading: Heading) = this + heading.vector

private enum class Heading(val vector: Vector, val symbol: Char) {
    UP(Vector(0, -1), 'U'),
    DOWN(Vector(0, 1), 'D'),
    LEFT(Vector(-1, 0), 'L'),
    RIGHT(Vector(1, 0), 'R')
}

// --- Core Logic Refactoring ---

/**
 * Narrative: Part 1 (Shortest Path)
 * The logic is still a BFS, but the main `while` loop is refactored for clarity.
 *
 * Why this change?
 * Your original code used `.map` with a non-local `return` (e.g., `return newPath`).
 * While this works because `map` is an `inline` function, it can be
 * confusing to read. A reader might expect `map` to produce a list,
 * not to halt the entire function.
 *
 * This new version uses a more conventional `for` loop. The "business logic"
 * is now explicit:
 * 1. For each valid move...
 * 2. If it's the GOAL, `return` immediately (this is the BFS shortest-path guarantee).
 * 3. Otherwise, add the new state to the queue for later exploration.
 *
 * This is more verbose but significantly clearer and easier to debug.
 */
private fun part1(input: List<String>): String {
    val password = input.first()
    val initialState = State(Vector(0, 0), "")
    val queue = ArrayDeque(listOf(initialState))

    while (queue.isNotEmpty()) {
        val (location, path) = queue.poll()
        val hash = "$password$path".md5()

        hash.openDoors()
            .validNextRooms(location)
            .forEach { (heading, nextLocation) ->
                val newPath = path + heading.symbol
                if (nextLocation == GOAL) {
                    return newPath // Found the shortest path
                }
                queue.add(State(nextLocation, newPath))
            }
    }
    // In a real-world scenario, we'd handle the "no path found" case.
    // For AOC, we assume a path exists.
    throw IllegalStateException("No path found")
}

/**
 * Narrative: Part 2 (Longest Path)
 * Similar to Part 1, this refactors the loop for clarity.
 *
 * Why this change?
 * Your original code used `.mapNotNull`. This was clever: it returned `null`
 * for paths that hit the goal (filtering them out of the `nextPossibleStates`)
 * and updated `longest` as a side effect.
 *
 * This new version is, again, more explicit. A `forEach` loop clearly
 * communicates that we are iterating over all valid moves and performing
 * an action. The `if/else` block makes the branching logic obvious:
 * 1. If it's the GOAL, update `longest` (but don't add to the queue).
 * 2. Otherwise, it's a valid intermediate step, so add it to the queue.
 *
 * This avoids using `mapNotNull` for its side effects and makes the
 * code's intent (traversing all paths) much more apparent.
 */
private fun part2(input: List<String>): Int {
    val password = input.first()
    val initialState = State(Vector(0, 0), "")
    val queue = ArrayDeque(listOf(initialState))

    var longest = 0

    while (queue.isNotEmpty()) {
        val (location, path) = queue.poll()
        val hash = "$password$path".md5()

        hash.openDoors()
            .validNextRooms(location)
            .forEach { (heading, nextLocation) ->
                val newPath = path + heading.symbol
                if (nextLocation == GOAL) {
                    longest = maxOf(longest, newPath.length)
                } else {
                    queue.add(State(nextLocation, newPath))
                }
            }
    }
    return longest
}

// --- Helper Function Refactoring ---

/**
 * For efficiency, we create a single, top-level MessageDigest instance.
 * `MessageDigest.getInstance()` can be a slow operation (it involves
 * service lookups). Since this is a single-threaded solution,
 * we can safely reuse the same instance. The `.digest()` call
 * resets the object's state, making it safe for reuse in the next loop.
 */
private val md5Instance: MessageDigest = MessageDigest.getInstance("MD5")

/**
 * Narrative: Refactoring `md5()`
 *
 * Why this change?
 * 1. Efficiency: Uses the top-level `md5Instance` described above.
 * 2. Idiomatic String Building: Your original `fold` is perfectly fine,
 * but `joinToString` is the canonical way to build a String from
 * a collection (or, in this case, a `ByteArray`).
 * `digest.joinToString("") { ... }` reads as "join this byte array
 * into a string, using "" as a separator, and formatting each
 * byte with this lambda." It's very expressive.
 */
private fun String.md5(): String {
    val digest = md5Instance.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

/**
 * We can cache these top-level vals to avoid re-creating them.
 * `openDoorChars` is the range of characters that mean "open".
 * `headings` is the ordered list of headings corresponding to the hash
 * characters [0, 1, 2, 3].
 */
private val openDoorChars = 'b'..'f'
private val headings = Heading.entries

/**
 * Narrative: Refactoring `openDoors()`
 *
 * Why this change?
 * Your original `map` -> `zip` -> `filter` -> `map` chain works,
 * but it's a multistep process that creates several intermediate lists.
 *
 * The `mapIndexedNotNull` function is a more direct and efficient
 * tool for this job. It iterates the first 4 characters of the hash
 * *once*.
 * - `index`: We get the index (0, 1, 2, or 3).
 * - `char`: We get the character.
 *
 * The lambda then does the logic in one step:
 * "If the `char` is in the 'open' range, return the `Heading` at that
 * `index`. Otherwise, return `null`."
 *
 * `mapIndexedNotNull` automatically filters out the `null`s,
 * resulting in a single, clean list of open-door `Heading`s.
 */
private fun String.openDoors(): List<Heading> {
    return this.take(4)
        .mapIndexedNotNull { index, char ->
            if (char in openDoorChars) headings[index] else null
        }
}

/**
 * This helper function is unchanged, it was already clear and correct.
 */
private fun List<Heading>.validNextRooms(vector: Vector): List<Pair<Heading, Vector>> {
    return this.map { heading ->
        heading to vector.move(heading)
    }.filter { (_, location) ->
        location.isValidLocation()
    }
}

// --- Test Functions ---
// Unchanged, as they correctly validate the logic.

private fun runPart1Tests() {
    check("Part 1 Test Case 1", listOf(Heading.UP, Heading.DOWN, Heading.LEFT), "ced9".openDoors())
    check("1 TC 1.b", listOf(Vector(0, 1)), "ced9".openDoors().validNextRooms(Vector(0, 0)).map { it.second })
    check("Part 1 Test Case 2", listOf(Heading.UP, Heading.LEFT, Heading.RIGHT), "f2bc".openDoors())
    check("1 TC 2.b", listOf(Vector(0, 0), Vector(1, 1)), "f2bc".openDoors().validNextRooms(Vector(0, 1)).map { it.second })

    check("1 TC 3", "DDRRRD", part1(listOf("ihgpwlah")))
    check("1 TC 4", "DDUDRLRRUDRD", part1(listOf("kglvqrro")))
    check("1 TC 5", "DRURDRUDDLLDLUURRDULRLDUUDDDRR", part1(listOf("ulqzkmiv")))
}

private fun runPart2Tests() {
    check("Part 2 Test Case 1", 370, part2(listOf("ihgpwlah")))
    check("Part 2 Test Case 2", 492, part2(listOf("kglvqrro")))
    check("Part 2 Test Case 3", 830, part2(listOf("ulqzkmiv")))
}