package com.sphericalchickens.aoc2016gem.day14

// We keep the necessary imports, adding ConcurrentHashMap for our cache
// and standardizing on coroutine primitives.
import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * We'll keep your MD5 extension function exactly as-is.
 * It's correct, efficient, and a perfect use case for an extension.
 */
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.fold("") { str, byte -> str + "%02x".format(byte) }
}

/**
 * Your main function is an excellent test harness. We'll adapt it slightly.
 * Since our refactored Part 1 and Part 2 will both be `suspend` functions
 * (to accommodate the unified `suspend isKey` function), we can call
 * them directly from within the `runBlocking` scope.
 */
fun main() = runBlocking {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 14 ---")

    val input = readInputLines("aoc2016/day14_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            // Note: part1 is now a suspend function
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
            // Note: part2 is now a suspend function
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private suspend fun runPart1Tests() = coroutineScope {
    val testInput = """
        abc
    """.trimIndent().lines()

    check("Part 1 Test Case 1", 22728, part1(testInput))
}

/**
 * Refactored Part 1. It's now a suspend function.
 * We define a simple, memoized hash function (`getHash`) and pass it to
 * our shared `isKey` checker. The core search logic remains a
 * simple and efficient `while` loop.
 */
private suspend fun part1(input: List<String>): Int {
    val salt = input.first()

    // A simple cache to store hashes. This is memoization.
    val hashCache = mutableMapOf<Int, String>()

    // This lambda is our "hashing strategy" for Part 1.
    // It checks the cache first, and only computes the hash if it's not found.
    // It's defined as `suspend` to match the required signature,
    // even though it does no async work itself.
    val getHash: suspend (Int) -> String = { index ->
        hashCache.computeIfAbsent(index) {
            "$salt$it".md5()
        }
    }

    var count = 0
    var key = -1
    while (count < 64) {
        key += 1
        // We call our new, unified `isKey` function
        if (key.isKey(getHash)) {
            count += 1
        }
    }

    return key
}

private suspend fun runPart2Tests() = coroutineScope {
    val testInput = """
        abc
    """.trimIndent().lines()

    // We can test the cache directly if we want
    val cache = StretchedHashCache(this, testInput.first())
    check("Part 2 Test Case 1 (Hash 0)", "a107ff634856bb300138cac6568c0f24", cache.getHash(0))

    // Run the full Part 2 test
    check("Part 2 Test Case 2 (Final Index)", 22551, part2(testInput))
}

/**
 * Refactored Part 2. It is now a `suspend` function, inheriting its
 * CoroutineScope from its caller (`main`).
 *
 * We preserve your excellent parallel-batching strategy, which is
 * crucial for performance. The only change is that we now pass
 * the `StretchedHashCache.getHash` method to our unified `isKey` function.
 */
private suspend fun part2(input: List<String>): Int = coroutineScope {
    val keys = sortedSetOf<Int>()
    val step = 5_000 // Your batch size is a good heuristic
    val salt = input.first()

    // Instantiate our cache, passing in the coroutine scope
    val cache = StretchedHashCache(this, salt)

    // This is a method reference. We are passing the `getHash` function
    // from our cache instance as the hashing strategy.
    val getHash: suspend (Int) -> String = cache::getHash

    var nextBlock = 0

    while (keys.size < 64) {
        val f = nextBlock
        val s = nextBlock + step
        nextBlock += step

        "Running next block: $f to $s".println()
        "Found ${keys.size} so far".println()

        // This parallel search logic is excellent and preserved.
        // It launches `step` (5000) coroutines in parallel to check for keys.
        (f until s).map { key ->
            async {
                // Each coroutine calls our unified `isKey` function.
                // The underlying `getHash` calls will be automatically
                // coordinated and memoized by the StretchedHashCache.
                if (key.isKey(getHash)) key else null
            }
        }.awaitAll()
            .filterNotNull() // Collect only the successful keys (non-null)
            .forEach { keys.add(it) }
    }

    // Return the 64th key (index 63)
    return@coroutineScope keys.elementAt(63)
}

/**
 * This is your `AsyncHashCache`, renamed for clarity and slightly cleaned up.
 * Its internal logic was already excellent.
 *
 * - It uses a `ConcurrentHashMap` for thread-safe caching.
 * - It stores `Deferred<String>` to ensure the hash for any given
 * index is only *ever* computed once.
 * - It uses `CoroutineStart.LAZY` so the computation doesn't even
 * start until it's first `.await()`ed.
 *
 * This is a textbook-perfect pattern for this kind of problem.
 */
private class StretchedHashCache(
    private val scope: CoroutineScope,
    private val salt: String
) {
    // The key is the index Int, not a String, for efficiency.
    private val cache = ConcurrentHashMap<Int, Deferred<String>>()

    suspend fun getHash(index: Int): String {
        // Get the existing task or create a new one atomically.
        val deferred = cache.computeIfAbsent(index) {
            // This lambda just *creates* the task, it doesn't run it.
            scope.async(Dispatchers.Default, CoroutineStart.LAZY) {
                // This block is where the actual work happens.
                calculateStretchedHash(index)
            }
        }
        // This await() either starts the lazy task, waits for an
        // in-progress task, or returns instantly if it's already done.
        return deferred.await()
    }

    private fun calculateStretchedHash(index: Int): String {
        var h = "$salt$index".md5()
        // The problem specifies 2016 *additional* hashes
        repeat(2016) {
            h = h.md5()
        }
        return h
    }
}

/**
 * The single, unified `isKey` function. It is `suspend` and
 * accepts a `getHash` function as a parameter.
 * This is the core of our refactoring.
 */
private suspend fun Int.isKey(getHash: suspend (Int) -> String): Boolean {
    // 1. Get the hash for the current index.
    val hash = getHash(this)

    // 2. Find the first triplet.
    val magicChar = hash.firstTriplet() ?: return false // Not a key if no triplet

    // 3. Check the next 1000 indices.
    // The `any` function is sequential, but it short-circuits:
    // it will stop as soon as it finds a single match.
    // The `getHash` calls within it are suspendable and will
    // benefit from the cache (for both Part 1 and Part 2).
    return ((this + 1)..(this + 1000)).any { lookAheadIndex ->
        getHash(lookAheadIndex).hasQuintuplet(magicChar)
    }
}

/**
 * A more idiomatic version of `firstTriplet` using `windowed`.
 * It creates a sliding window of 3 characters and looks for
 * the first window where all characters are the same.
 */
private fun String.firstTriplet(): Char? {
    return this.windowed(size = 3)
        .firstOrNull { it[0] == it[1] && it[1] == it[2] }
        ?.get(0) // If found, return the character
}

/**
 * Your `hasQuintuplet` function is perfectly clear and efficient.
 * No changes needed.
 */
private fun String.hasQuintuplet(ch: Char): Boolean {
    val needle = ch.toString().repeat(5)
    return this.contains(needle)
}