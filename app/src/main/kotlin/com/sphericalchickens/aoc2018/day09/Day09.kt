package com.sphericalchickens.aoc2018.day09

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
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }

    // inputElves = input.mapIndexed { index, snacks -> toElf(index, snacks) }
  }

  class Node(val value: Int) {
    lateinit var prev: Node
    lateinit var next: Node
  }

  fun part1() {
    val lastMarble = 70904
    val numPlayers = 473

    playMarbleGame(numPlayers, lastMarble)
  }

  private fun playMarbleGame(numPlayers: Int, lastMarble: Int) {
    var currentPlayer = 0

    val scores = LongArray(numPlayers)

    var nextMarbleValue = 0
    var currentMable = Node(nextMarbleValue++)
    currentMable.next = currentMable
    currentMable.prev = currentMable

    // Remember the zeroMarble for later
    val zeroMarble = currentMable
    // printRound(currentPlayer, currentMable, zeroMarble)

    while (nextMarbleValue <= lastMarble) {
      currentPlayer = (currentPlayer + 1) % numPlayers
      val newNode = Node(nextMarbleValue++)
      if (newNode.value % 23 != 0) {
        val insertionSpot = currentMable.next.next

        newNode.next = insertionSpot
        newNode.prev = insertionSpot.prev

        newNode.prev.next = newNode
        newNode.next.prev = newNode

        currentMable = newNode
      } else {
        scores[currentPlayer] += newNode.value.toLong()
        var iter = currentMable
        repeat(7) {
          iter = iter.prev
        }

        iter.prev.next = iter.next
        iter.next.prev = iter.prev

        scores[currentPlayer] += iter.value.toLong()
        currentMable = iter.next
      }
      // printRound(currentPlayer, currentMable, zeroMarble)
    }

    println(scores.maxOf { it })
  }

  private fun printRound(currentPlayer: Int, currentMable: Node, zeroMarble: Node) {
    print("[$currentPlayer] ")

    var iter = zeroMarble

    do {
      val v = if (currentMable.value == iter.value) {
        "(${iter.value})"
      } else {
        " ${iter.value} "
      }
      print(v.padStart(4, ' '))

      iter = iter.next
    } while(iter.value != 0)
    println()

  }

  fun part2() {
    val lastMarble = 7090400
    val numPlayers = 473

    playMarbleGame(numPlayers, lastMarble)
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
    println("--- Advent of Code 2018, Day 9 ---")
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
