package com.sphericalchickens.aoc2022.day25

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue


import java.util.TreeSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val day = 25
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    1=-0-2
    12111
    2=0=
    21
    2=01
    111
    20012
    112
    1=-1=
    1-12
    12
    1=
    122
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }.filter { it.isNotBlank() }
  }

  fun part1() {
    val answer = input.sumOf { parse(it) }
    println(answer)
    println(toSnafu(answer))
  }

  private fun toSnafu(value0: Long): String {
    val output = mutableListOf<Char>()
    var value = value0

    while (value > 0) {
      val remainder = (value % 5).toInt()
      value = (value / 5)

      if (remainder < 3) {
        output.add('0' + remainder)
      } else {
        value += 1
        if (remainder == 3) {
          output.add('=')
        } else {
          output.add('-')
        }
      }
    }

    return output.reversed().joinToString("")
  }

  private fun parse(line: String): Long {
    return line.map { c ->
      when (c) {
        '-' -> -1
        '=' -> -2
        else -> c - '0'
      }
    }.fold(0) { sum, value ->
      sum * 5 + value
    }
  }

  fun part2() {
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


fun main() {
    println("--- Advent of Code 2022, Day 25 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}
