package com.sphericalchickens.aoc2015.day05

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 05: Doesn't He Have Intern-Elves For This?
 *
 * This program solves the puzzle for Day 05
 * It contains at least three vowels (aeiou only), like aei, xazegov, or aeiouaeiouaeiou.
 * It contains at least one letter that appears twice in a row, like xx, abcdde (dd), or aabbccdd (aa, bb, cc, or dd).
 * It does not contain the strings ab, cd, pq, or xy, even if they are part of one of the other requirements.
 *
 * ugknbfddgicrmopn is nice because it has at least three vowels (u...i...o...), a double letter (...dd...), and none of the disallowed substrings.
 * aaa is nice because it has at least three vowels and a double letter, even though the letters used by different rules overlap.
 * jchzalrnumimnmhp is naughty because it has no double letter.
 * haegwjzuvuyypxyu is naughty because it contains the string xy.
 * dvszwmarrgswjxmb is naughty because it contains only one vowel.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day05_input.txt")
    println("\n--- Advent of Code 2015, Day 05 ---")

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
    return input.count { it.isNice() }
}

fun part2(input: List<String>): Int {
    return input.count { it.isNice2() }
}

private fun String.isNice2(): Boolean =
    hasNonOverlappingRepeat() && hasNonConsecutiveRepeatingLetter()

private fun String.isNice(): Boolean {
    return hasThreeVowels() && hasDoubleLetter() && freeOfForbiddenCombos()
}

private val vowels = "aeiou".toSet()

private fun String.hasThreeVowels(): Boolean {
    return this.count { it in vowels } >= 3
}

private fun String.hasDoubleLetter(): Boolean {
    return this.toList().windowed(2, 1).firstOrNull { it.first() == it.last() } != null
}

private val forbiddenPhrases = "ab,cd,pq,xy".split(",")

private fun String.freeOfForbiddenCombos(): Boolean {
    return forbiddenPhrases.none { this.contains(it) }
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    listOf(
        "aei", "xazegov", "aeiouaeiouaeiou"
    ).forEach {
        check(it.hasThreeVowels())
    }

    listOf(
        "xx", "abcdde", "aabbccdd"
    ).forEach {
        check(it.hasDoubleLetter())
    }

    mapOf(
        "ugknbfddgicrmopn" to true,
        "aaa" to true,
        "jchzalrnumimnmhp" to false,
        "haegwjzuvuyypxyu" to false,
        "dvszwmarrgswjxmb" to false,
    ).forEach { (key, value) ->
        check(key.isNice() == value) {
            "$key was expected to be $value"
        }
    }

    // Part 2 Test Cases
    check("xyxy".hasNonOverlappingRepeat())
    check(!"aaa".hasNonOverlappingRepeat())

    check("xyx".hasNonConsecutiveRepeatingLetter())
    check("abcdefeghi".hasNonConsecutiveRepeatingLetter())
    check("aaa".hasNonConsecutiveRepeatingLetter())

    check("qjhvhtzxzqqjkmpb".hasNonOverlappingRepeat())
    check("xxyxx".hasNonConsecutiveRepeatingLetter())

    mapOf(
        "qjhvhtzxzqqjkmpb" to true,
        "xxyxx" to true,
        "uurcxstgmygtbstg" to false,
        "ieodomkazucvgmuy" to false,
    ).forEach { (key, value) ->
        check(key.isNice2() == value) {
            "$key was expected to be $value:" + "${key.hasNonOverlappingRepeat()}  ${key.hasNonConsecutiveRepeatingLetter()}"
        }
    }
}

private fun String.hasNonOverlappingRepeat(): Boolean {
    /*
    It contains a pair of any two letters that appears at least twice in the string without overlapping, like xyxy (xy) or aabcdefgaa (aa), but not like aaa (aa, but it overlaps).
     */
    val couples = this.windowed(2, 1).withIndex()
    val groupedMap = couples.groupBy({ it.value }, { it.index })

    return groupedMap.values.any { indices ->
        indices.last() - indices.first() > 1
    }
}

private fun String.hasNonConsecutiveRepeatingLetter(): Boolean {
    /*
    It contains at least one letter which repeats with exactly one letter between them, like xyx, abcdefeghi (efe), or even aaa
     */

    val letterMap = this.toList().withIndex().groupBy({ it.value }, { it.index })
    return letterMap.values.any { indices ->
        indices.any { indices.contains(it + 2) }
    }
}