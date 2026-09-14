package com.sphericalchickens.aoc2022.day03

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 3
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>
  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    vJrwpWtwJgWrhcsFMMfFFhFp
    jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
    PmmdzqPrVvPwwTWBwg
    wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
    ttgJtRGJQctTZtZT
    CrZsJsPPZsGzwwsLwLmpwMDw
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val rucksacks = input.map {
      val s = it.length / 2
      it.windowed(s, s).map(String::toSet)
    }

    val answer = rucksacks.sumOf { (a, b) ->
      a.intersect(b).first().toPriority()
    }

    println(answer)
  }

  fun part2() {
    val answer = input.windowed(3, 3)
      .map { group -> group.map { it.toSet() } }
      .sumOf { (a, b, c) -> a.intersect(b).intersect(c).first().toPriority() }

    println(answer)
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

private fun Char.toPriority(): Int {
  return when (this) {
    in 'a'..'z' -> this - 'a' + 1
    in 'A'..'Z' -> this - 'A' + 27
    else -> 0
  }
}


fun main() {
    println("--- Advent of Code 2022, Day 3 ---")
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
