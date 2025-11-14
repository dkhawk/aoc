package com.sphericalchickens.aoc2016.day14

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
import kotlin.system.measureTimeMillis
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.fold("") { str, byte -> str + "%02x".format(byte) }
}

fun main() = runBlocking {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
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

private suspend fun runPart1Tests() = coroutineScope {
    val testInput = """
        abc
    """.trimIndent().lines()

    check("Part 1 Test Case 1", 22728, part1(testInput))
}

private fun Int.isKey(salt: String, hashFunction: (String, Int) -> String): Boolean {
    val h = hashFunction(salt, this)
    return h.firstTriplet()?.let { magic ->
        val f = ((this + 1) .. (this + 1000)).firstOrNull { it.hasQuintuplet(salt, magic) }
        f != null
    } ?: false
}

private fun Int.hasQuintuplet(salt: String, magic: Char): Boolean {
    return this.createHash(salt).hasQuintuplet(magic)
}

private fun Int.createHash(salt: String): String {
    return createHash(salt, this)
}

private fun String.hasQuintuplet(ch: Char): Boolean {
    val needle = ch.toString().repeat(5)
    return this.contains(needle)
}

private fun createHash(salt: String, key: Int) : String {
    return "$salt$key".md5()
}

private fun String.firstTriplet(): Char? {
    for (i in 0 .. (this.lastIndex - 2)) {
        if (this[i] == this[i + 1] && this[i] == this[i + 2]) {
            return this[i]
        }
    }

    return null
}

private fun runPart2Tests() = runBlocking {
    val testInput = """
        abc
    """.trimIndent().lines()

    val cache = AsyncHashCache(this, testInput.first())
    check("Part 2 Test Case 1", "a107ff634856bb300138cac6568c0f24", 0.getHash(cache))
    part2(testInput).println()
}

private fun part1(input: List<String>): Int {
    val salt = input.first()

    var count = 0
    var key = -1
    while (count < 64) {
        key += 1
        if (key.isKey(salt) { s,k -> k.createHash(s) } ) {
            count += 1
        }
    }

    return key
}

private fun part2(input: List<String>): Int {
    val keys = sortedSetOf<Int>()

    val step = 5_000

    runBlocking {
        val salt = input.first()

        val cache = AsyncHashCache(this, salt)
        var nextBlock = 0

        while (keys.size < 64) {
            val f = nextBlock
            val s = nextBlock + step
            nextBlock += step

            "Running next block: $f to $s".println()
            "Found ${keys.size} so far".println()

            (f until s).map { key ->
                async {
                    key to key.isKeyNew(cache)
                }
            }.awaitAll().filter { it.second }.map { it.first }.forEach { keys.add(it) }
        }
    }

    return keys.drop(63).first()
}

private suspend fun Int.isKeyNew(cache: AsyncHashCache): Boolean {
    val magic = this.getHash(cache).firstTriplet() ?: return false

    return ((this + 1) .. (this + 1000)).any {
        it.getHash(cache).hasQuintuplet(magic)
    }
}

// This class encapsulates the logic you want.
// It needs a CoroutineScope to launch the calculation tasks.
// In Android, you would pass viewModelScope.
class AsyncHashCache(private val scope: CoroutineScope, private val salt: String) {

    // 1. A thread-safe cache.
    // 2. It stores the *task* (Deferred) that PRODUCES the hash,
    //    not the hash string itself.
    private val cache = ConcurrentHashMap<String, Deferred<String>>()

    /**
     * An extension function on Int to get its hash.
     * This is thread-safe and guarantees the calculation
     * for a given (Int, salt) pair only ever runs once.
     */
    suspend fun Int.getHash(): String {
        // Create a unique key for the (Int, salt) pair
        val key = "$this:$salt"

        // 3. Atomically get the existing task or create a new one.
        val deferred = cache.computeIfAbsent(key) {
            // This lambda is a NORMAL, non-suspend function.
            // All we do is *create* a new, lazy task.

            // 4. Create the task, but don't start it yet (CoroutineStart.LAZY).
            //    It will run on the Default dispatcher when it's first .await()ed.
            scope.async(Dispatchers.Default, CoroutineStart.LAZY) {
                // This block IS a suspend block.
                this@getHash.calculateHash() // Call the real work
            }
        }

        // 5. Wait for the task to complete.
        //    - If the task was new (LAZY), .await() starts it and waits.
        //    - If the task was already running (started by another
        //      coroutine), .await() just waits for it to finish.
        //    - If the task was already complete, .await() returns instantly.
        return deferred.await()
    }

    /**
     * The actual, expensive calculation.
     */
    private fun Int.calculateHash(): String {
        var h = "$salt$this"
        repeat(2017) {
            h = h.md5()
        }

        return h
    }
}

// A helper to make the syntax `42.getHash(cache, "foo")` work
suspend fun Int.getHash(cache: AsyncHashCache): String {
    return cache.run { this@getHash.getHash() }
}