@file:OptIn(ExperimentalStdlibApi::class)

package com.sphericalchickens.aoc2022.day05

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration

import com.sphericalchickens.utils.*
import java.util.Stack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 5
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
"""

  private lateinit var moves: List<Move>
  private lateinit var stacks: List<List<Char>>

  init {
  }

  @Template("move #0 from #1 to #2")
  data class Move(val quantity: Int, val source: Int, val destination: Int)

  fun initialize() {
    val input = if (useRealData) InputNew(year, day).readAsString() else sampleInput

    val (stacksInput, movesInput) = input.split("\n\n")

    stacks = parseStacks(stacksInput)

    val inputFactory = InputFactory(Move::class)
    moves = movesInput.split("\n").mapNotNull { inputFactory.lineToClass<Move>(it) }.map {
      it.copy(source = it.source - 1, destination = it.destination - 1)
    }
  }

  private fun parseStacks(stacksInput: String): MutableList<MutableList<Char>> {
    val lines = stacksInput.split("\n").filter { it.isNotBlank() }.dropLast(1)

    val stacks = lines.map { line ->
      line.windowed(4, 4, true).map { it[1] }
    }.transpose()

    stacks.forEach { stack ->
      stack.removeIf { !it.isLetter() }
    }

    return stacks
  }

  fun part1() {
    val mutableStacks = stacks.map { ArrayDeque(it) }

    moves.forEach { move ->
      val source = mutableStacks[move.source]
      val destination = mutableStacks[move.destination]
      repeat(move.quantity) {
        destination.add(source.removeLast())
      }
    }

    val answer = mutableStacks.map { it.last() }.joinToString("")
    println(answer)
  }

  fun part2() {
    val mutableStacks = stacks.map { ArrayDeque(it) }

    moves.forEach { move ->
      val source = mutableStacks[move.source]
      val destination = mutableStacks[move.destination]
      val load = ArrayDeque<Char>(100)
      repeat(move.quantity) {
        load.add(source.removeLast())
      }
      repeat(move.quantity) {
        destination.add(load.removeLast())
      }
    }

    val answer = mutableStacks.map { it.last() }.joinToString("")
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

private fun List<List<Char>>.transpose(): MutableList<MutableList<Char>> {
  val stacks = mutableListOf<MutableList<Char>>()
  repeat(this.last().size) {
    stacks.add(mutableListOf())
  }

  this.reversed().forEach { line ->
    line.forEachIndexed { index, c -> stacks[index].add(c) }
  }

  return stacks
}


fun main() {
    println("--- Advent of Code 2022, Day 5 ---")
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
