/**
 * This file contains different implementations for solving the "staircase problem".
 * The problem is to find the number of ways to climb a staircase of `n` steps,
 * given that you can take a certain number of steps at a time.
 * In this case, you can take 1, 3, or 5 steps at a time.
 *
 * The file provides five different implementations:
 * 1. A naive recursive solution (`countStepsNoCache`).
 * 2. A memoized recursive solution using BigInteger (`countSteps`).
 * 3. A concurrent memoized recursive solution using coroutines (`countSteps2`).
 * 4. A bottom-up dynamic programming solution using Long (`countStepsBottomUp`).
 * 5. A bottom-up dynamic programming solution using BigInteger (`countStepsBottomUpBigInt`).
 *
 * The `main` function benchmarks these implementations.
 */
package com.sphericalchickens.computerphile.stairs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val steps = 100L
    // Benchmark the naive recursive solution (commented out as it's too slow)
//    val t0 = measureTimeMillis {
//        val result = countStepsNoCache(steps)
//        println("Naive recursive result: $result")
//    }
//    println("Naive recursive time: $t0 ms")

    // Benchmark the memoized recursive solution with BigInteger
    val t1 = measureTimeMillis {
        val result = countSteps(BigInteger.valueOf(steps))
        println("Memoized recursive (BigInt) result: $result")
    }
    println("Memoized recursive (BigInt) time: $t1 ms\n")

    // Benchmark the concurrent memoized recursive solution
    val t2 = measureTimeMillis {
        withContext(Dispatchers.Default) {
            val result = countSteps2(this, steps)
            println("Concurrent memoized (Long) result: ${result.await()}")
        }
    }
    println("Concurrent memoized (Long) time: $t2 ms\n")

    // Benchmark the bottom-up dynamic programming solution with Long
    val t3 = measureTimeMillis {
        val result = countStepsBottomUp(steps.toInt())
        println("Bottom-up DP (Long) result: $result")
    }
    println("Bottom-up DP (Long) time: $t3 ms\n")

    // Benchmark the bottom-up dynamic programming solution with BigInteger
    val t4 = measureTimeMillis {
        val result = countStepsBottomUpBigInt(steps.toInt())
        println("Bottom-up DP (BigInt) result: $result")
    }
    println("Bottom-up DP (BigInt) time: $t4 ms")
}

/**
 * Calculates the number of ways to climb a staircase with `step` steps,
 * taking 1, 3, or 5 steps at a time.
 * This is a naive recursive solution without memoization.
 *
 * @param step The number of steps in the staircase.
 * @return The number of ways to climb the staircase.
 */
fun countStepsNoCache(step: Long) : Long {
    return when {
        step < 0 -> 0
        step == 0L -> 1
        else -> countStepsNoCache(step - 5) + countStepsNoCache(step - 3) + countStepsNoCache(step - 1)
    }
}

val cache = mutableMapOf<BigInteger, BigInteger>()

/**
 * Calculates the number of ways to climb a staircase with `step` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses memoization and BigInteger to handle large results.
 *
 * @param step The number of steps in the staircase.
 * @return The number of ways to climb the staircase.
 */
val jumps = listOf(
    BigInteger.valueOf(5),
    BigInteger.valueOf(3),
    BigInteger.valueOf(1),
)
fun countSteps(step: BigInteger) : BigInteger {
    return cache.getOrPut(step) {
        when {
            step < BigInteger.ZERO -> BigInteger.ZERO
            step == BigInteger.ZERO -> BigInteger.ONE
            else -> jumps.map { countSteps(step - it) }
                .fold(BigInteger.ZERO) { acc, result -> acc + result }
        }
    }
}

val c2 = ConcurrentHashMap<Long, Deferred<Long>>()

/**
 * Calculates the number of ways to climb a staircase with `step` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses memoization and coroutines to solve the problem concurrently.
 * The cache is a [ConcurrentHashMap] to ensure thread safety.
 *
 * @param scope The coroutine scope to launch new coroutines in.
 * @param step The number of steps in the staircase.
 * @return A [Deferred] value representing the number of ways to climb the staircase.
 */
fun countSteps2(scope: CoroutineScope, step: Long) : Deferred<Long> {
    val diffs = listOf(5, 3, 1)

    return c2.getOrPut(step) {
        scope.async {
            when {
                step < 0 -> 0
                step == 0L -> 1
                else -> diffs.map { countSteps2(this, step - it) }.awaitAll().sum()
            }
        }
    }
}

/**
 * Calculates the number of ways to climb a staircase with `steps` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses a bottom-up dynamic programming approach with Long.
 *
 * @param steps The number of steps in the staircase.
 * @return The number of ways to climb the staircase (will overflow for large numbers).
 */
fun countStepsBottomUp(steps: Int): Long {
    if (steps < 0) return 0
    // dp[i] will store the number of ways to reach step i
    val dp = LongArray(steps + 1)
    // Base case: There is one way to reach step 0 (by not taking any steps)
    dp[0] = 1

    val stepSizes = listOf(1, 3, 5)

    for (i in 1..steps) {
        for (stepSize in stepSizes) {
            if (i >= stepSize) {
                dp[i] += dp[i - stepSize]
            }
        }
    }
    return dp[steps]
}

/**
 * Calculates the number of ways to climb a staircase with `steps` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses a bottom-up dynamic programming approach with BigInteger.
 *
 * @param steps The number of steps in the staircase.
 * @return The number of ways to climb the staircase.
 */
fun countStepsBottomUpBigInt(steps: Int): BigInteger {
    if (steps < 0) return BigInteger.ZERO
    // dp[i] will store the number of ways to reach step i
    val dp = Array(steps + 1) { BigInteger.ZERO }
    // Base case: There is one way to reach step 0
    dp[0] = BigInteger.ONE

    val stepSizes = listOf(1, 3, 5)

    for (i in 1..steps) {
        for (stepSize in stepSizes) {
            if (i >= stepSize) {
                dp[i] = dp[i].add(dp[i - stepSize])
            }
        }
    }
    return dp[steps]
}
