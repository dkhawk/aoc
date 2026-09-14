package com.sphericalchickens.aoc2018.day02

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    abcdef
    bababc
    abbcde
    abcccd
    aabcdd
    abcdee
    ababab 
  """.trimIndent().split("\n")

  val sampleInput2 = """
    abcde
    fghij
    klmno
    pqrst
    fguij
    axcye
    wvxyz
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput2
    }
  }

  fun part1() {
    val x = input.map { line ->
      val counts = line.trim().groupBy { it }.values.map { it.size }
      counts.contains(2) to counts.contains(3)
    }.unzip().toList().map { it.count { it } }

    println(x.first() * x.last())
  }

  fun part2() {
    var minDist = Int.MAX_VALUE
    var best = "" to ""

    input.forEachIndexed { index, s ->
      if (index < input.lastIndex) {
        input.subList(index + 1, input.size).forEach { other ->
          val d = s.distance(other)
          if (d < minDist) {
            minDist = d
            best = s to other
          }
        }
      }
    }

    println(minDist)
    println(best)
    println(best.first.intersection(best.second))
  }

  fun execute() {
    job?.cancel()
    job = scope.launch {
      running = true
      running = false
    }
  }

  fun step() {
  }

  fun stop() {
    job?.cancel()
    running = false
  }

  fun reset() {
    stop()
  }

  fun updateDataSource(useRealData: Boolean) {
    this.useRealData = useRealData
    initialize()
    reset()
  }
}

private fun String.intersection(other: String): String {
  return zip(other).mapNotNull { (a, b) -> if (a == b) a else null }.joinToString("")
}

private fun String.distance(other: String): Int {
  return zip(other).map { (a, b) -> a != b }.count { it }
}


fun main() {
    println("--- Advent of Code 2018, Day 2 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    val (_, p1Duration) = measureTimedValue {
        try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Solving Part 2:")
    val (_, p2Duration) = measureTimedValue {
        try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
    }
    println("Part 2 runtime: ${formatDuration(p2Duration)}")
}
