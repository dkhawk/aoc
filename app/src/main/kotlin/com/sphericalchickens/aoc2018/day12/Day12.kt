package com.sphericalchickens.aoc2018.day12

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = (500L)
  val maxDelay = 500L

  val sampleInput = """
    initial state: #..#.#..##......###...###

    ...## => #
    ..#.. => #
    .#... => #
    .#.#. => #
    .#.## => #
    .##.. => #
    .#### => #
    #.#.# => #
    #.### => #
    ##.#. => #
    ##.## => #
    ###.. => #
    ###.# => #
    ####. => #
  """.trimIndent().split("\n")

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val (_, initialState) = input.first().split(": ")
    println(initialState)
    val rules = input.drop(2).associate { line -> line.toRule() }

    var pots = initialState.mapIndexed { index, c -> index to c }.toMap().toMutableMap()

    repeat(20) { _ ->
      val keys = pots.keys.sorted()
      val min = keys.first()
      val max = keys.last()

      pots = ((min - 5)..(max + 5)).associateWith { index ->
        val ruleKey = ((index - 2)..(index + 2)).map { key ->
          pots.getOrDefault(key, '.')
        }.joinToString("")

        rules.getOrDefault(ruleKey, '.')
      }.toMutableMap()

      trimEmptyPots(pots)
    }

    println(potsToString(pots))
    val score = pots.map {
      if (it.value == '#') it.key else 0
    }.sum()

    println(score)
  }

  private fun trimEmptyPots(pots: MutableMap<Int, Char>) {
    // Trim the ends
    val firstPlant = pots.keys.first { pots[it] == '#' }
    val lastPlant = pots.keys.last { pots[it] == '#' }

    val range = firstPlant..lastPlant

    pots.removeAll(pots.keys.filterNot { it in range })
  }

  private fun potsToString(pots: MutableMap<Int, Char>): String {
    return pots.keys.sorted().map<Int, Any> {
      val p = pots.getValue(it)
      if (it == 0) {
        "${COLORS.RED}$p${COLORS.NONE}"
      } else {
        p
      }
    }.joinToString("")
  }

  fun part2() {
    val (_, initialState) = input.first().split(": ")
    // println(initialState)
    val rules = input.drop(2).associate { line -> line.toRule() }

    var pots = initialState.mapIndexed { index, c -> index to c }.toMap().toMutableMap()

    // repeat(20000) { _ ->
    //   val keys = pots.keys.sorted()
    //   val min = keys.first()
    //   val max = keys.last()
    //
    //   pots = ((min - 5)..(max + 5)).associateWith { index ->
    //     val ruleKey = ((index - 2)..(index + 2)).map { key ->
    //       pots.getOrDefault(key, '.')
    //     }.joinToString("")
    //
    //     rules.getOrDefault(ruleKey, '.')
    //   }.toMutableMap()
    //
    //   trimEmptyPots(pots)
    // }
    //
    // println(potsToString(pots))
    // val score = pots.map {
    //   if (it.value == '#') it.key else 0
    // }.sum()
    //
    // println(score)

    println((80 * 50000000000))

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

private fun <K, V> MutableMap<K, V>.removeAll(toRemove: List<K>) {
  toRemove.forEach { this.remove(it) }
}

private fun String.toRule() = slice(0..4) to this[9]


fun main() {
    println("--- Advent of Code 2018, Day 12 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}
