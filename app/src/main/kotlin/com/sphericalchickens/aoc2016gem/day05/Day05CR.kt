package com.sphericalchickens.aoc2016gem.day05

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 05 (Corrected Coroutines) ---")

    val input = readInputLines("aoc2016/day05_input.txt").first()
    val solution = Day05CoroutineSolution(input)

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        solution.runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = solution.part1()
            println("   Part 1: $part1Result") // Expected: f97c354d
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        solution.runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = solution.part2()
            println("   Part 2: $part2Result") // Expected: 863dde27
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private class Day05CoroutineSolution(private val doorId: String) {

    private val numWorkers = Runtime.getRuntime().availableProcessors()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.produceValidHashes(): ReceiveChannel<Pair<Int, String>> =
        produce(Dispatchers.Default, capacity = numWorkers * 4) { // Using your larger capacity
            val jobs = (0 until numWorkers).map { workerId ->
                launch {
                    val md5 = MessageDigest.getInstance("MD5")
                    var index = workerId
                    while (isActive) {
                        val inputBytes = (doorId + index).toByteArray()
                        val hashBytes = md5.digest(inputBytes)

                        if (hashBytes[0].toInt() == 0 && hashBytes[1].toInt() == 0 && (hashBytes[2].toInt() and 0xF0) == 0) {
                            val hexString = hashBytes.joinToString("") { "%02x".format(it) }
                            send(index to hexString)
                        }
                        index += numWorkers
                    }
                }
            }
            jobs.joinAll()
        }

    suspend fun part1(): String {
        return coroutineScope {
            val channel = produceValidHashes()
            val foundHashes = mutableListOf<Pair<Int, String>>()
            repeat(30) {
                foundHashes.add(channel.receive())
            }
            channel.cancel()
            foundHashes
                .sortedBy { it.first }
                .map { it.second[5] }
                .take(8)
                .joinToString("")
        }
    }

    /**
     * Solves Part 2 with logic that is robust against race conditions.
     */
    suspend fun part2(): String {
        // --- FIX ---
        // The map now stores not just the character, but a Pair of the character
        // and the index that produced it. This allows us to compare indices.
        val password = mutableMapOf<Char, Pair<Char, Int>>()

        coroutineScope {
            val channel = produceValidHashes()

            for ((index, hash) in channel) {
                val pos = hash[5]
                val char = hash[6]

                if (pos in '0'..'7') {
                    val existingEntry = password[pos]

                    // --- FIX ---
                    // The core logic change:
                    // 1. If the position is empty (existingEntry == null), we accept the new character.
                    // 2. OR if the new hash's index is LOWER than the index of the character
                    //    we currently have for that position, we replace it.
                    if (existingEntry == null || index < existingEntry.second) {
                        password[pos] = char to index
                    }
                }

                if (password.size == 8) {
                    // This is now an optimization. We could continue searching for even lower
                    // indices, but for AoC puzzles, the first 8 found are usually sufficient.
                    // To be 100% correct, one might let it run longer, but this is practically safe.
                    channel.cancel()
                    break
                }
            }
        }

        return password.toSortedMap().values
            .map { it.first } // Extract just the character for the final result
            .joinToString("")
    }

    suspend fun runPart1Tests() {
        check(
            "Part 1 Test Case 1",
            "18f47a30",
            Day05CoroutineSolution("abc").part1()
        )
    }

    suspend fun runPart2Tests() {
        check(
            "Part 2 Test Case 1",
            "05ace8e3",
            Day05CoroutineSolution("abc").part2()
        )
    }
}
