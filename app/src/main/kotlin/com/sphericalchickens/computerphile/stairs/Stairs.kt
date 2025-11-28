/**
 * This file contains different implementations for solving the "staircase problem".
 * The problem is to find the number of ways to climb a staircase of `n` steps,
 * given that you can take a certain number of steps at a time.
 * In this case, you can take 1, 3, or 5 steps at a time.
 *
 * The file provides three different implementations:
 * 1. A naive recursive solution (`countStepsNoCache`).
 * 2. A memoized recursive solution (`countSteps`).
 * 3. A concurrent memoized recursive solution using coroutines (`countSteps2`).
 *
 * The `main` function benchmarks these three implementations.
 */
package com.sphericalchickens.computerphile.stairs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    // Benchmark the naive recursive solution
    val t0 = measureTimeMillis {
        val result = countStepsNoCache(50)
        println(result)
    }

    println(t0)

    // Benchmark the memoized recursive solution
    val t1 = measureTimeMillis {
        val result = countSteps(50)
        println(result)
    }

    println(t1)

    // Benchmark the concurrent memoized recursive solution
    val t2 = measureTimeMillis {
        withContext(Dispatchers.Default) {
            val result = countSteps2(this, 50)
            println(result.await())
        }
    }

    println(t2)
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

val cache = mutableMapOf<Long, Long>()

val c2 = mutableMapOf<Long, Deferred<Long>>()

/**
 * Calculates the number of ways to climb a staircase with `step` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses memoization to cache the results of subproblems.
 *
 * @param step The number of steps in the staircase.
 * @return The number of ways to climb the staircase.
 */
fun countSteps(step: Long) : Long {
    return cache.getOrPut(step) {
        when {
            step < 0 -> 0
            step == 0L -> 1
            else -> countSteps(step - 5) + countSteps(step - 3) + countSteps(step - 1)
        }
    }
}

/**
 * Calculates the number of ways to climb a staircase with `step` steps,
 * taking 1, 3, or 5 steps at a time.
 * This solution uses memoization and coroutines to solve the problem concurrently.
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
