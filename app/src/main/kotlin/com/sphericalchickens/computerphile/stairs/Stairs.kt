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
    val t0 = measureTimeMillis {
        val result = countStepsNoCache(50)
        println(result)
    }

    println(t0)

    val t1 = measureTimeMillis {
        val result = countSteps(50)
        println(result)
    }

    println(t1)

    val t2 = measureTimeMillis {
        withContext(Dispatchers.Default) {
            val result = countSteps2(this, 50)
            println(result.await())
        }
    }

    println(t2)
}

fun countStepsNoCache(step: Long) : Long {
    return when {
        step < 0 -> 0
        step == 0L -> 1
        else -> countStepsNoCache(step - 5) + countStepsNoCache(step - 3) + countStepsNoCache(step - 1)
    }
}

val cache = mutableMapOf<Long, Long>()

val c2 = mutableMapOf<Long, Deferred<Long>>()

fun countSteps(step: Long) : Long {
    return cache.getOrPut(step) {
        when {
            step < 0 -> 0
            step == 0L -> 1
            else -> countSteps(step - 5) + countSteps(step - 3) + countSteps(step - 1)
        }
    }
}

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
