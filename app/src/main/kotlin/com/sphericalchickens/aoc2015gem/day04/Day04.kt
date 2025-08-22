package com.sphericalchickens.aoc2015gem.day04


import com.sphericalchickens.utils.readInputLines
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest

/**
 * # Advent of Code 2015, Day 4: The Ideal Stocking Stuffer
 *
 * This program finds the smallest positive integer that, when appended to a secret key,
 * produces an MD5 hash starting with a certain number of zeroes.
 */
fun main() = runBlocking {
    println("--- Advent of Code 2015, Day 04 ---")

    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Puzzle Input ---
    val puzzleInput = readInputLines("aoc2015/day04_input.txt").first()
    println("\nSecret key: $puzzleInput")

    // --- Part 1: Find a hash starting with five zeroes ---
    val part1Result = findAdventCoin(puzzleInput, "00000")
    println("🎁 Part 1 (five zeroes): $part1Result")

    // --- Part 2: Find a hash starting with six zeroes ---
    val part2Result = findAdventCoin(puzzleInput, "000000")
    println("🎀 Part 2 (six zeroes): $part2Result")
}

/**
 * To avoid creating a new MessageDigest instance for every hash, which is expensive,
 * and to ensure thread safety when running concurrently, we use a ThreadLocal.
 * This provides each thread with its own instance of the MD5 digest.
 */
private val md5Digest = ThreadLocal.withInitial { MessageDigest.getInstance("MD5") }

/**
 * An extension function to convert a ByteArray into its hexadecimal string representation.
 * This is used as a fallback if the byte-level check doesn't apply.
 */
private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * Checks if the MD5 hash of `key + number` starts with the given `prefix`.
 *
 * This function is optimized to check the raw bytes of the hash digest directly,
 * which is much faster than converting the entire hash to a hex string first.
 *
 * @param key The secret key string.
 * @param number The integer to append to the key.
 * @param prefix The required starting string of the hex hash (e.g., "00000").
 * @return True if the hash matches the prefix, false otherwise.
 */
private fun hashStartsWith(key: String, number: Int, prefix: String): Boolean {
    val input = "$key$number".toByteArray()
    val digest = md5Digest.get().digest(input)

    // Optimized checks for the specific prefixes required by the puzzle.
    return when (prefix) {
        "00000" -> {
            // "00000" means the first 2.5 bytes are zero.
            // This translates to:
            // - The first byte is 0x00.
            // - The second byte is 0x00.
            // - The high 4 bits of the third byte are 0 (i.e., the byte is between 0x00 and 0x0F).
            digest[0] == 0.toByte() && digest[1] == 0.toByte() && (digest[2].toInt() and 0xF0) == 0
        }
        "000000" -> {
            // "000000" means the first 3 bytes are zero.
            digest[0] == 0.toByte() && digest[1] == 0.toByte() && digest[2] == 0.toByte()
        }
        // A generic fallback for any other prefix.
        else -> digest.toHexString().startsWith(prefix)
    }
}

/**
 * Finds the lowest positive integer that produces a hash with the desired prefix.
 *
 * It performs a parallel search by launching multiple coroutines, each responsible
 * for checking a specific range of numbers. It proceeds in large batches until a
 * solution is found.
 *
 * @param key The secret key.
 * @param prefix The required hash prefix.
 * @return The smallest integer that satisfies the condition.
 */
suspend fun findAdventCoin(key: String, prefix: String): Int = coroutineScope {
    var searchStart = 1
    val coroutines = 100 // Number of coroutines to launch per batch.
    val batchSize = 2000  // Numbers each coroutine will check.

    var answer: Int

    // Loop indefinitely, searching in large batches, until a result is found.
    while (true) {
        // Launch a batch of coroutines. Each one searches its own range of numbers.
        val winners = (0 until coroutines).map { i ->
            async {
                val range = (searchStart + i * batchSize) until (searchStart + (i + 1) * batchSize)
                // Within each coroutine, find the *first* number in its range that matches.
                range.firstOrNull { number -> hashStartsWith(key, number, prefix) }
            }
        }.awaitAll() // Wait for all coroutines in the batch to complete.

        // After the batch completes, find the smallest winning number, if any.
        val result = winners.filterNotNull().minOrNull()
        if (result != null) {
            answer = result
            break
        }

        // If no winner was found in this batch, advance our starting point and search again.
        searchStart += coroutines * batchSize
    }

    answer
}

/**
 * Executes checks to validate the core logic against the known examples from the puzzle description.
 */
private suspend fun runTests() {
    check(findAdventCoin("abcdef", "00000") == 609043)
    check(findAdventCoin("pqrstuv", "00000") == 1048970)
}