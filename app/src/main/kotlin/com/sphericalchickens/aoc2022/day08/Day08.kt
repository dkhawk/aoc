package com.sphericalchickens.aoc2022.day08

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import java.util.PriorityQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 8
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: NewGrid<Int>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    30373
    25512
    65332
    33549
    35390
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  init {
  }

  fun initialize() {
    val lines = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }

    input = NewGrid(lines.first().length, lines.size, lines.joinToString("").toList().map { it - '0' })
  }

  fun part1() {
    // Get each row and column
    val visibleTrees = mutableSetOf<Vector>()

    input.forEachRowIndexed { rowIndex, row ->
      visibleTrees.addAll(
        getVisibleTrees(row).map {
          Vector(it, rowIndex)
        }
      )
      visibleTrees.addAll(
        getVisibleTrees(row.reversed()).map {
          Vector((input.width - 1) - it, rowIndex)
        }
      )
    }

    input.forEachColumnIndexed { colIndex, col ->
      visibleTrees.addAll(
        getVisibleTrees(col).map {
          Vector(colIndex, it)
        }
      )
      visibleTrees.addAll(
        getVisibleTrees(col.reversed()).map {
          Vector(colIndex, (input.height - 1) - it)
        }
      )
    }

    println(visibleTrees.size)

    // val s = input.mapRowIndexed { rowIndex, row ->
    //   row.withIndex().joinToString("") { (colIndex, value) ->
    //     val location = Vector(colIndex, rowIndex)
    //     if (visibleTrees.contains(location)) {
    //       "[$value]"
    //     } else {
    //       " $value "
    //     }
    //   }
    // }
    // println(s.joinToString("\n"))
  }

  private fun getVisibleTrees(trees: List<Int>): MutableList<Int> {
    // start from the beginning
    val visible = mutableListOf<Int>()

    val iterator = trees.withIndex().iterator()
    var max = -1

    while (iterator.hasNext()) {
      val (index, tree) = iterator.next()
      if (tree > max) {
        visible.add(index)
        max = tree
      }
    }

    return visible
  }

  fun part2() {
    val scores = PriorityQueue<Int>(compareByDescending { it })

    (0 until input.height).forEach { rowIndex ->
      (0 until input.width).forEach { colIndex ->
        scores.add(
          viewingScore(getViewingDistances(colIndex, rowIndex))
        )
      }
    }

    println(scores.first())
  }

  private fun viewingScore(viewingDistances: List<Int>): Int {
    return viewingDistances.fold(1) { a, b ->
      a * b
    }
  }

  private fun getViewingDistances(colIndex: Int, rowIndex: Int): List<Int> {
    val location = Vector(colIndex, rowIndex)
    return Heading.values().map { heading ->
      getViewingDistanceForDirection(location, heading)
    }
  }

  private fun getViewingDistanceForDirection(
    location: Vector,
    heading: Heading,
  ): Int {
    val targetTree = input.getValue(location)
    var next = location
    var result = 0
    while (true) {
      next = next.advance(heading)
      if (input.validLocation(next)) {
        result += 1
        if (input.getValue(next) >= targetTree) {
          break
        }
      } else {
        break
      }
    }
    return result
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
    println("--- Advent of Code 2022, Day 8 ---")
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
