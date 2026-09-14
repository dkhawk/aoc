package com.sphericalchickens.aoc2021.day07

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import kotlin.math.abs
import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day07 {
  companion object {
    fun run() {
      Day07().part1()
//      Day07().part2()
    }
  }

  val sample = """16,1,2,0,4,2,7,1,2,14""".split(",").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): List<Int> {
    return if (useRealInput) {
      Input.readAsLines("07").first().split(",").filter { it.isNotBlank() }
    } else {
      sample
    }.map { it.toInt() }

  }

  private fun part1() {
    val crabs = getInput(useRealInput = true)

//    println(crabs.filter { it in 30..127 }.map { it.toChar() }.joinToString(""))

    val max = crabs.maxOf { it }
    val min = crabs.minOf { it }

    val best = minCost(min, max, crabs) { start, end ->
      abs(end - start).toLong()
    }

    println(best)
  }

  private fun part2() {
    val crabs = getInput(useRealInput = true)

    val max = crabs.maxOf { it }
    val min = crabs.minOf { it }

    // Needs to be initialized to one since the first value of the sequence is the seed
    var index = 1L
    val costValues = generateSequence(0L) {
      it + (index++)
    }.take(max + 1).toList()

    val best = minCost(min, max, crabs) { start, end ->
      costValues[abs(end - start)]
    }

    println(best)
  }

  private fun minCost(
    min: Int,
    max: Int,
    crabs: List<Int>,
    costFunction: (Int, Int) -> Long
  ): Pair<Int, Long>? {
    return (min..max).map { position ->
      position to crabs.sumOf { costFunction(it, position) }
    }.minByOrNull { it.second }
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 7 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day07.run()
        } catch (e: Exception) {
            println("Error running Day07: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}
