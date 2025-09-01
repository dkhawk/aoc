package com.sphericalchickens.aoc2015gem.day10

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 10: TBD
 *
 * This program solves the puzzle for Day 10.
 *
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day10_input.txt")
    println("\n--- Advent of Code 2015, Day XX ---")


    // --- Part 1: TBD ---
    val part1Result = part1(puzzleInput)
    println("🎁 Part 1: $part1Result")


    // --- Part 2: TBD ---
    val part2Result = part2(puzzleInput)
    println("🎀 Part 2: $part2Result")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------
fun part1(input: List<String>): Int {
    return applyLookAndSay(input.first(), 40)
}

fun part2(input: List<String>): Int {
    return applyLookAndSay(input.first(), 50)
}

/**
 * Applies the "look-and-say" process a specified number of times.
 *
 * @param initialString The starting string for the sequence.
 * @param iterations The number of times to apply the process.
 * @return The length of the resulting string.
 */
private fun applyLookAndSay(initialString: String, iterations: Int): Int {
    // generateSequence creates an infinite lazy sequence starting with 'initialString'.
    // Each subsequent element is the result of applying 'lookAndSay' to the previous one.
    val finalString = generateSequence(initialString, ::lookAndSay)
        .drop(iterations) // We drop the first 'iterations' elements...
        .first()         // ...and take the next one, which is the result after n steps.

    return finalString.length
}

/**
 * Performs one round of the "look-and-say" sequence transformation.
 * For example: "111221" becomes "312211".
 */
private fun lookAndSay(input: String): String {
    // buildString is highly efficient for creating strings in a loop,
    // avoiding the creation of many intermediate String objects.
    return buildString {
        var i = 0
        while (i < input.length) {
            val char = input[i]
            var count = 1
            // Look ahead to count consecutive matching characters.
            while (i + count < input.length && input[i + count] == char) {
                count++
            }
            // Append the count and the character to our result.
            append(count)
            append(char)
            // Move the index past the group of characters we just processed.
            i += count
        }
    }
}

private fun lookAndSayRegex(input: String): String {
    val regex = """(.)\1*""".toRegex()
    return regex.findAll(input).joinToString("") { matchResult ->
        val group = matchResult.value
        "${group.length}${group.first()}"
    }
}

/**
 * Groups consecutive equal elements of a sequence into lists. This operation is lazy.
 *
 * This function is useful for tasks like run-length encoding or processing batches
 * of identical items.
 *
 * ### Example:
 * ```
 * val data = sequenceOf("a", "a", "b", "c", "c", "c", "a")
 * val grouped = data.groupConsecutive().toList()
 * // Result: [[a, a], [b], [c, c, c], [a]]
 * ```
 *
 * @return A new sequence where each element is a list containing a run of
 * consecutive, equal elements from the source sequence. Returns an
 * empty sequence if the source is empty.
 */
fun <T> Sequence<T>.groupConsecutive(): Sequence<List<T>> {
    // The 'sequence' builder creates a lazily evaluated sequence. The code
    // inside this block only executes as the consumer requests items.
    return sequence {
        val iterator = this@groupConsecutive.iterator()

        // Handle the edge case of an empty source sequence.
        if (!iterator.hasNext()) {
            return@sequence
        }

        // Start the first group with the first element.
        var currentGroup = mutableListOf(iterator.next())

        // Process the rest of the sequence.
        for (element in iterator) {
            // If the current element matches the first element of our group,
            // add it to the group.
            if (element == currentGroup.first()) {
                currentGroup.add(element)
            } else {
                // If it doesn't match, the current group is complete.
                // Yield it to the consumer...
                yield(currentGroup)
                // ...and start a new group with the current element.
                currentGroup = mutableListOf(element)
            }
        }

        // After the loop finishes, the last group has been built but not yet
        // yielded, so we must yield it here.
        yield(currentGroup)
    }
}

// Using the helper
private fun lookAndSayGrouping(input: String): String {
    return input.asSequence()
        .groupConsecutive() // Hypothetical extension function
        .map { group -> "${group.size}${group.first()}" }
        .joinToString("")
}

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testCases = mapOf(
        "1" to "11",
        "11" to "21",
        "21" to "1211",
        "1211" to "111221",
        "111221" to "312211"
    )

    testCases.forEach { (input, expected) ->
        val actual = lookAndSay(input)
        check(actual == expected) { "FAIL: lookAndSay($input) was '$actual', expected '$expected'" }
    }

    testCases.forEach { (input, expected) ->
        val actual = lookAndSayRegex(input)
        check(actual == expected) { "FAIL: lookAndSay($input) was '$actual', expected '$expected'" }
    }

    testCases.forEach { (input, expected) ->
        val actual = lookAndSayRegex(input)
        check(actual == expected) { "FAIL: lookAndSayGrouping($input) was '$actual', expected '$expected'" }
    }
}