package com.sphericalchickens.app.aoc2015.day04

import com.sphericalchickens.utils.readInputLines
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * # Advent of Code 2015, Day 4: The Ideal Stocking Stuffer
 *
 * Santa needs help mining some AdventCoins (very similar to bitcoins) to use as gifts for all the economically forward-thinking little girls and boys.
 *
 * To do this, he needs to find MD5 hashes which, in hexadecimal, start with at least five zeroes. The input to the MD5 hash is some secret key
 * (your puzzle input, given below) followed by a number in decimal. To mine AdventCoins, you must find Santa the lowest positive number (no leading zeroes: 1, 2, 3, ...) that produces such a hash.
 *
 * For example:
 *
 * If your secret key is abcdef, the answer is 609043, because the MD5 hash of abcdef609043 starts with five zeroes (000001dbbfa...), and it is the lowest such number to do so.
 * If your secret key is pqrstuv, the lowest number it combines with to make an MD5 hash starting with five zeroes is 1048970; that is, the MD5 hash of pqrstuv1048970 looks like 000006136ef....
 *
 *
 */
fun main() = runBlocking {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")
    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day04_input.txt").first()
    println("\n--- Advent of Code 2015, Day 04 ---")


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

val md = MessageDigest.getInstance("MD5")

suspend fun part1(input: String): String = coroutineScope {
    mine(input)  { t -> has5LeadingZeros(input, t) }
}

suspend fun mine(key: String, block: (Int) -> Boolean): String = coroutineScope {
    var trial = 0
    val blockSize = 1000 // Let each job look at this many hashes

    while (true) {
        val x = (0 until 100).map {
            async {
                val range = trial until (trial + blockSize)
                trial += blockSize
                hashOfBlock(key, range, block)
            }
        }.awaitAll().filterNotNull().minOrNull()

        if (x != null) {
            trial = x
            break
        }
    }

    trial.toString()
}

fun hashOfBlock(key: String, ints: IntRange, block: (Int) -> Boolean): Int? {
    return ints.firstOrNull(block)
}

private fun has5LeadingZeros(key: String, hypothesis: Int): Boolean {
    val combinedInput = key + hypothesis.toString()
    val digest = md.digest(combinedInput.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }.substring(0 until 5) == "00000"
}

private fun has6LeadingZeros(key: String, hypothesis: Int): Boolean {
    val combinedInput = key + hypothesis.toString()
    val digest = md.digest(combinedInput.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }.substring(0 until 6) == "000000"
}

suspend fun part2(input: String): String = coroutineScope {
    mine(input)  { t -> has6LeadingZeros(input, t) }
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private suspend fun runTests() {
    check(has5LeadingZeros("abcdef", 609043))
    check(has5LeadingZeros("pqrstuv", 1048970))

    check(!has5LeadingZeros("abcdef", 1))
    check(!has5LeadingZeros("pqrstuv", 1))

    // Part 1 Test Cases
    mapOf(
        "abcdef" to "609043",
        "pqrstuv" to "1048970"
    ).forEach { (key, answer) ->
        check(part1(key) == answer)
    }
}
