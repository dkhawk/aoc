package com.sphericalchickens.app.aoc2015gem.day05

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 05: Doesn't He Have Intern-Elves For This?
 *
 * This solution determines which strings in a list are "nice" based on two different sets of rules.
 * We approach this using a literate programming style, where the code and its explanation are interwoven.
 * The solution is broken down into small, readable functions with clear responsibilities, making the
 * logic easy to follow from the problem description to the final implementation.
 */
fun main() {
    // --- Verification ---
    // Before solving the puzzle, we run a suite of tests to ensure our logic is correct.
    // This is a crucial step to validate our interpretation of the rules.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // We load the puzzle input from the provided file.
    val puzzleInput = readInputLines("aoc2015/day05_input.txt")
    println("\n--- Advent of Code 2015, Day 05 ---")

    // --- Part 1 ---
    // We apply the first set of rules to the input and count how many strings qualify as "nice".
    val part1Result = puzzleInput.count(String::isNiceByPart1Rules)
    println("🎁 Part 1: A total of $part1Result strings are nice under the old rules.")

    // --- Part 2 ---
    // We apply the second, more complex set of rules and perform the count again.
    val part2Result = puzzleInput.count(String::isNiceByPart2Rules)
    println("🎀 Part 2: A total of $part2Result strings are nice under the new rules.")
}

// =============================================================================================
// Part 1: The Original Naughty or Nice List
// =============================================================================================

/**
 * A string is "nice" according to the part 1 rules if it meets three criteria:
 * 1. It contains at least three vowels (`a`, `e`, `i`, `o`, `u`).
 * 2. It contains at least one letter that appears twice in a row.
 * 3. It does not contain the substrings "ab", "cd", "pq", or "xy".
 */
private fun String.isNiceByPart1Rules(): Boolean {
    return hasAtLeastThreeVowels() &&
            hasRepeatingAdjacentLetter() &&
            isFreeOfForbiddenSubstrings()
}

/**
 * Checks if the string contains at least three vowels.
 * We define the vowels in a `Set` for efficient lookup and simply count their occurrences.
 */
private fun String.hasAtLeastThreeVowels(): Boolean {
    val vowels = setOf('a', 'e', 'i', 'o', 'u')
    return this.count { it in vowels } >= 3
}

/**
 * Checks for any character immediately followed by the same character (e.g., "xx").
 * An idiomatic Kotlin approach is to use `zipWithNext()`, which creates pairs of adjacent
 * characters. We then check if any pair consists of two identical characters.
 */
private fun String.hasRepeatingAdjacentLetter(): Boolean {
    return this.zipWithNext().any { (char1, char2) -> char1 == char2 }
}

/**
 * Checks that the string does not contain any forbidden two-character combinations.
 * We iterate through our predefined list of forbidden strings and ensure none are present.
 * The `none` function is perfect for this, as it stops as soon as a match is found.
 */
private fun String.isFreeOfForbiddenSubstrings(): Boolean {
    val forbidden = listOf("ab", "cd", "pq", "xy")
    return forbidden.none { this.contains(it) }
}

// =============================================================================================
// Part 2: The New, Improved Naughty or Nice List
// =============================================================================================

/**
 * In part 2, the rules for a "nice" string change entirely:
 * 1. It contains a pair of any two letters that appears at least twice without overlapping.
 * 2. It contains at least one letter which repeats with exactly one letter between them.
 */
private fun String.isNiceByPart2Rules(): Boolean {
    return hasNonOverlappingPair() && hasRepeatingLetterWithOneInBetween()
}

/**
 * This rule looks for a two-letter sequence that repeats elsewhere in the string.
 * For example, `xyxy` is valid (`xy`), but `aaa` is not (the `aa` pairs overlap).
 * A regular expression is a powerful and declarative way to find this pattern.
 *
 * The regex `(..).*\\1` breaks down as:
 * - `(..)`: Match any two characters and capture them as group 1.
 * - `.*`: Match any number of characters (the gap between the pairs).
 * - `\\1`: Match the exact text that was captured by group 1.
 */
private fun String.hasNonOverlappingPairRegex(): Boolean {
    return "(..).*\\1".toRegex().containsMatchIn(this)
}

/**
 * This rule looks for a two-letter sequence that repeats elsewhere in the string.
 * For example, `xyxy` is valid (`xy`), but `aaa` is not (the `aa` pairs overlap).
 *
 * We achieve this by iterating through all possible two-letter pairs. For each pair
 * starting at index `i`, we check if the remainder of the string (starting from `i + 2`
 * to prevent overlaps) contains that same pair.
 */
private fun String.hasNonOverlappingPair(): Boolean {
    if (this.length < 4) return false
    for (i in 0..this.length - 4) {
        val pair = this.substring(i, i + 2)
        if (this.substring(i + 2).contains(pair)) {
            return true
        }
    }
    return false
}

/**
 * This rule looks for a character that is repeated, separated by a single character.
 * For example, `xyx` (`x`), `abcdefeghi` (`efe`), or `aaa` (`a`).
 * Again, a regular expression provides a concise and readable solution.
 *
 * The regex `(.).\\1` breaks down as:
 * - `(.)`: Match any single character and capture it as group 1.
 * - `.`: Match any single character (the one in between).
 * - `\\1`: Match the exact text that was captured by group 1.
 */
private fun String.hasRepeatingLetterWithOneInBetweenRegex(): Boolean {
    return "(.).\\1".toRegex().containsMatchIn(this)
}

/**
 * This rule looks for a character that is repeated, separated by a single character.
 * For example, `xyx` (`x`), `abcdefeghi` (`efe`), or `aaa` (`a`).
 *
 * A clean way to check this is to create a sliding window of three characters across
 * the string and check if the first and last characters of any window are the same.
 */
private fun String.hasRepeatingLetterWithOneInBetween(): Boolean {
    return this.windowed(3).any { it[0] == it[2] }
}


// =============================================================================================
// Verification Logic
// =============================================================================================

/**
 * Executes checks to validate the core logic against the examples from the puzzle description.
 * This ensures our functions behave as expected before we use them on the real input.
 */
private fun runTests() {
    // Part 1 Test Cases
    check("ugknbfddgicrmopn".isNiceByPart1Rules())
    check("aaa".isNiceByPart1Rules())
    check(!"jchzalrnumimnmhp".isNiceByPart1Rules())
    check(!"haegwjzuvuyypxyu".isNiceByPart1Rules())
    check(!"dvszwmarrgswjxmb".isNiceByPart1Rules())

    // Part 2 Test Cases
    check("qjhvhtzxzqqjkmpb".isNiceByPart2Rules())
    check("xxyxx".isNiceByPart2Rules())
    check(!"uurcxstgmygtbstg".isNiceByPart2Rules())
    check(!"ieodomkazucvgmuy".isNiceByPart2Rules())

    // Individual function tests for part 2 to be thorough
    check("xyxy".hasNonOverlappingPair())
    check(!"aaa".hasNonOverlappingPair())
    check("aabcdefgaa".hasNonOverlappingPair())
    check("xyx".hasRepeatingLetterWithOneInBetween())
    check("abcdefeghi".hasRepeatingLetterWithOneInBetween())
    check("aaa".hasRepeatingLetterWithOneInBetween())
}
